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

package im.turms.server.common.infra.lang;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author James Chen
 */
public final class StringBuilderPool {

    private static final FastThreadLocal<StringBuilderWriter> WRITER = new FastThreadLocal<>() {
        @Override
        protected StringBuilderWriter initialValue() {
            return new StringBuilderWriter(512, 10 * 1024);
        }
    };

    private StringBuilderPool() {
    }

    public static StringBuilderWriter getWriter() {
        return WRITER.get();
    }

}
