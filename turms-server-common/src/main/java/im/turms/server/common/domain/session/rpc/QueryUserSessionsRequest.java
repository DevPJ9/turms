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

package im.turms.server.common.domain.session.rpc;

import im.turms.server.common.domain.session.bo.UserSessionsInfo;
import im.turms.server.common.domain.session.service.ISessionService;
import im.turms.server.common.infra.cluster.service.rpc.NodeTypeToHandleRpc;
import im.turms.server.common.infra.cluster.service.rpc.dto.RpcRequest;
import lombok.Data;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

/**
 * @author James Chen
 */
@Data
public class QueryUserSessionsRequest extends RpcRequest<List<UserSessionsInfo>> {

    private static ISessionService sessionService;

    private final Set<Long> userIds;

    public QueryUserSessionsRequest(Set<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public String name() {
        return "queryUserSessions";
    }

    @Override
    public NodeTypeToHandleRpc nodeTypeToRequest() {
        return NodeTypeToHandleRpc.SERVICE;
    }

    @Override
    public NodeTypeToHandleRpc nodeTypeToRespond() {
        return NodeTypeToHandleRpc.GATEWAY;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        if (sessionService == null) {
            sessionService = getBean(ISessionService.class);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public List<UserSessionsInfo> call() {
        return sessionService.getUserSessions(userIds);
    }

}
