/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.server.common.infra.address;

import im.turms.server.common.infra.net.InetAddressUtil;
import im.turms.server.common.infra.property.TurmsPropertiesManager;
import io.netty.handler.codec.http.HttpStatusClass;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James Chen
 * @implNote Don't cache the public IP because it may change over time.
 */
@Component
public class PublicIpManager {

    private final TurmsPropertiesManager propertiesManager;
    private HttpClient client;

    public PublicIpManager(TurmsPropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

    public Mono<String> getPublicIp() {
        List<String> ipDetectorAddresses = propertiesManager.getLocalProperties().getIp().getPublicIpDetectorAddresses();
        if (ipDetectorAddresses.isEmpty()) {
            return Mono.error(new IllegalStateException("Failed to detect the public IP because cannot find an IP detector address"));
        }
        List<Mono<String>> monos = new ArrayList<>(ipDetectorAddresses.size());
        HttpClient httpClient = getClient();
        for (String ipDetectorAddress : ipDetectorAddresses) {
            Mono<String> ipMono = httpClient.get()
                    .uri(ipDetectorAddress)
                    .responseSingle((response, body) -> response.status().codeClass().equals(HttpStatusClass.SUCCESS)
                            ? body.asString()
                            .flatMap(ip -> {
                                ip = ip.trim();
                                return InetAddressUtil.isInetAddress(ip)
                                        ? Mono.just(ip)
                                        : Mono.empty();
                            })
                            : Mono.empty());
            monos.add(ipMono);
        }
        return Mono.firstWithValue(monos)
                .onErrorMap(t -> new IllegalStateException("Cannot find the public IP of the local node", t));
    }

    private synchronized HttpClient getClient() {
        if (client == null) {
            client = HttpClient.create();
        }
        return client;
    }

}