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

package im.turms.server.common.infra.reflect;

import im.turms.server.common.infra.unsafe.UnsafeUtil;
import lombok.Data;
import lombok.SneakyThrows;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

/**
 * @author James Chen
 */
@Data
public class UnsafeAndMethodHandledBasedVarAccessor<T, V> implements VarAccessor<T, V> {

    private static final Unsafe UNSAFE = UnsafeUtil.UNSAFE;

    private final Class<?> declaringClass;
    private final long fieldOffset;
    private final MethodHandle setter;

    public UnsafeAndMethodHandledBasedVarAccessor(Field field, MethodHandle setter) {
        declaringClass = field.getDeclaringClass();
        if (field.getType().isPrimitive()) {
            throw new IllegalArgumentException("The field type cannot be primitive");
        }
        if (declaringClass.isRecord()) {
            throw new IllegalArgumentException("The declaring class cannot be record");
        }
        fieldOffset = UNSAFE.objectFieldOffset(field);
        this.setter = setter;
    }

    @Override
    public V get(T object) {
        if (!declaringClass.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException("The object class should be the class or the subclass of: "
                    + declaringClass.getName()
                    + ". But actually it is: "
                    + object.getClass().getName());
        }
        return (V) UNSAFE.getObject(object, fieldOffset);
    }

    @SneakyThrows
    @Override
    public void set(T object, V value) {
        setter.invoke(object, value);
    }

}
