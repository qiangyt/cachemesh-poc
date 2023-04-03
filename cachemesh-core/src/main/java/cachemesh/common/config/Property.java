/*
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.common.config;

import java.lang.reflect.Method;

import cachemesh.common.util.Reflect;
import lombok.AccessLevel;
import lombok.Builder;

public class Property<T> {

    private final String propertyName;

    private final Method setter;

    private final Method getter;

    private final T defaultValue;

    private final Operator<? extends T> op;

    @Builder(access = AccessLevel.PUBLIC)
    public Property(Class<?> configClass, String propertyName, T defaultValue, Operator<? extends T> op) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.op = op;

        var propClass = op.propertyClass();
        this.setter = Reflect.setter(configClass, propertyName, propClass);
        this.getter = Reflect.getter(configClass, propertyName, propClass);
    }

    public T defaultValue() {
        return this.defaultValue;
    }

    public Operator<? extends T> op() {
        return this.op;
    }

    public String propertyName() {
        return this.propertyName;
    }

    public Method setter() {
        return this.setter;
    }

    public Method getter() {
        return this.getter;
    }

    @SuppressWarnings("unchecked")
    public T get(Object object) {
        try {
            return (T) getter().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String hint, Object object, Object value) {
        var v = op().convert(hint, value);

        try {
            setter().invoke(object, v);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
