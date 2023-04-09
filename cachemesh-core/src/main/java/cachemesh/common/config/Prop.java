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
import java.util.Map;

import cachemesh.common.misc.Reflect;
import lombok.Builder;

public class Prop<T> {

    private final String name;

    private final Method setter;

    private final Method getter;

    private final T devault;

    private final Op<? extends T> op;

    @Builder
    public Prop(Class<?> config, String name, T devault, Op<? extends T> op) {
        this.name = name;
        this.devault = devault;
        this.op = op;

        var propClass = op.type();
        this.setter = Reflect.setter(config, name, propClass);
        this.getter = Reflect.getter(config, name, propClass);
    }

    public T devault() {
        return this.devault;
    }

    public String name() {
        return this.name;
    }

    public Method setter() {
        return this.setter;
    }

    public Method getter() {
        return this.getter;
    }

    public Op<? extends T> op(Map<String, Object> object) {
        return this.op;
    }

    public T get(Object object) {
        return Reflect.get(getter(), object);
    }

    public Object readMap(Map<String, Object> map) {
        return map.get(name());
    }

    public void set(String hint, Map<String, Object> object) {
        var op = op(object);
        Object value = object.get(name());
        doSet(op, hint, object, value);
    }

    public void doSet(Op<? extends T> op, String hint, Map<String, Object> object, Object value) {
        var v = op.build(hint, object, value);
        Reflect.set(setter(), object, v);
    }

}
