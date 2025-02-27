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

package im.turms.server.common.domain.notification.rpc;

import im.turms.server.common.access.client.dto.constant.DeviceType;
import im.turms.server.common.domain.notification.service.INotificationService;
import im.turms.server.common.domain.session.bo.UserSessionId;
import im.turms.server.common.infra.cluster.service.rpc.NodeTypeToHandleRpc;
import im.turms.server.common.infra.cluster.service.rpc.dto.RpcRequest;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author James Chen
 */
@Data
public class SendNotificationRequest extends RpcRequest<Boolean> {

    private static final String NAME = "sendNotification";
    private static INotificationService notificationService;

    private final ByteBuf notificationBuffer;
    private final Set<Long> recipientIds;
    private final Set<UserSessionId> excludedUserSessionIds;
    @Nullable
    private final DeviceType excludedDeviceType;

    /**
     * @param notificationBuffer should be a direct byte buffer of TurmsNotification
     */
    public SendNotificationRequest(@NotNull ByteBuf notificationBuffer,
                                   @NotEmpty Set<Long> recipientIds,
                                   @NotNull Set<UserSessionId> excludedUserSessionIds,
                                   @Nullable DeviceType excludedDeviceType) {
        this.notificationBuffer = notificationBuffer;
        this.recipientIds = recipientIds;
        this.excludedUserSessionIds = excludedUserSessionIds;
        this.excludedDeviceType = excludedDeviceType;
        setBoundBuffer(notificationBuffer);
    }

    @Override
    public String name() {
        return NAME;
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
    public boolean isAsync() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        if (notificationService == null) {
            notificationService = getBean(INotificationService.class);
        }
    }

    /**
     * @return true if the notification has forwarded to one recipient at least
     */
    @Override
    public Boolean call() {
        return notificationService.sendNotificationToLocalClients(getTracingContext(),
                notificationBuffer,
                recipientIds,
                excludedUserSessionIds,
                excludedDeviceType);
    }

}
