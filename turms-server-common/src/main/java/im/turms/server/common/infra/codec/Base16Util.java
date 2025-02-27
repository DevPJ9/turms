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

package im.turms.server.common.infra.codec;

import im.turms.server.common.infra.lang.StringUtil;

/**
 * @author James Chen
 */
public final class Base16Util {

    private static final byte[] UPPER = StringUtil.getBytes("0123456789ABCDEF");

    private static final byte[] LOWER = StringUtil.getBytes("0123456789abcdef");

    private Base16Util() {
    }

    public static byte[] encode(byte[] bytes, boolean upper) {
        byte[] table = upper ? UPPER : LOWER;
        byte[] dst = new byte[bytes.length * 2];
        byte b;
        for (int i = 0, j = 0; i < bytes.length; i++) {
            b = bytes[i];
            dst[j++] = table[(b >> 4) & 0x0f];
            dst[j++] = table[b & 0x0f];
        }
        return dst;
    }

    public static String encodeAsString(byte[] src, boolean upper) {
        return StringUtil.newLatin1String(encode(src, upper));
    }

}