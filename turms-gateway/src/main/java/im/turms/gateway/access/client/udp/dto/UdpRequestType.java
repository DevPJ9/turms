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

package im.turms.gateway.access.client.udp.dto;

import javax.annotation.Nullable;

/**
 * @author James Chen
 */
public enum UdpRequestType {
    HEARTBEAT,
    GO_OFFLINE;

    private static final UdpRequestType[] ALL = UdpRequestType.values();

    @Nullable
    public static UdpRequestType parse(int number) {
        int index = number - 1;
        if (index > -1 && index < ALL.length) {
            return ALL[index];
        }
        return null;
    }

    public int getNumber() {
        return this.ordinal() + 1;
    }

}
