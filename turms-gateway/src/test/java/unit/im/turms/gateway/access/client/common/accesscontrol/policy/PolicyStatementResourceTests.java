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

package unit.im.turms.gateway.access.client.common.accesscontrol.policy;

import im.turms.gateway.access.client.common.authorization.policy.PolicyStatementResource;
import im.turms.server.common.access.client.dto.request.TurmsRequest;
import im.turms.server.common.infra.lang.Triple;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author James Chen
 */
class PolicyStatementResourceTests {

    @Test
    void test() {
        Set<TurmsRequest.KindCase> actualTypes = new HashSet<>();
        Set<Set<TurmsRequest.KindCase>> allTypes = new HashSet<>();
        for (PolicyStatementResource resource : PolicyStatementResource.values()) {
            for (Triple<String, Set<TurmsRequest.KindCase>, List<String>> triple : List.of(
                    Triple.of("creating", resource.getRequestTypesForCreating(), List.of("CREATE_")),
                    Triple.of("deleting", resource.getRequestTypesForDeleting(), List.of("DELETE_")),
                    Triple.of("updating", resource.getRequestTypesForUpdating(), List.of("UPDATE_")),
                    Triple.of("querying", resource.getRequestTypesForQuerying(), List.of("QUERY_", "CHECK_")))) {
                Set<TurmsRequest.KindCase> requestTypes = triple.second();
                actualTypes.addAll(requestTypes);
                if (!requestTypes.isEmpty() && !allTypes.add(requestTypes)) {
                    throw new IllegalArgumentException("The request types have been registered: " + requestTypes);
                }
                List<String> prefixes = triple.third();
                for (TurmsRequest.KindCase requestType : requestTypes) {
                    boolean startsWith = false;
                    for (String prefix : prefixes) {
                        if (requestType.name().startsWith(prefix)) {
                            startsWith = true;
                            break;
                        }
                    }
                    if (!startsWith) {
                        throw new IllegalArgumentException("The request types %s for %s should start with: %s"
                                .formatted(requestTypes, triple.first(), prefixes));
                    }
                }
            }
        }
        Set<TurmsRequest.KindCase> excludedRequestTypes = Set.of(TurmsRequest.KindCase.KIND_NOT_SET,
                TurmsRequest.KindCase.CREATE_SESSION_REQUEST,
                TurmsRequest.KindCase.DELETE_SESSION_REQUEST);
        for (TurmsRequest.KindCase requestType : TurmsRequest.KindCase.values()) {
            if (!excludedRequestTypes.contains(requestType)) {
                if (!actualTypes.contains(requestType)) {
                    throw new IllegalArgumentException("The request type is missing: " + requestType);
                }
            }
        }
    }
}