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

package im.turms.server.common.infra.property.env.gateway.identityaccessmanagement.http;

import im.turms.server.common.infra.property.constant.HttpAuthenticationHttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.Map;

/**
 * @author James Chen
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class HttpIdentityAccessManagementRequestProperties {

    private String url = "";

    private Map<String, String> headers = Collections.emptyMap();

    private HttpAuthenticationHttpMethod httpMethod = HttpAuthenticationHttpMethod.GET;

    @Min(1)
    private int timeoutMillis = 30 * 1000;

}