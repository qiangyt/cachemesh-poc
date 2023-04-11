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

public class ReflectProp<T> extends AbstractProp<T> {

    private final Method setter;

    private final Method getter;

    @Builder
    public ReflectProp(Class<?> config, String name, T devault, TypeOp<? extends T> op) {
        super(name, devault, op);

        var propClass = op.klass();
        this.setter = Reflect.setter(config, name, propClass);
        this.getter = Reflect.getter(config, name, propClass);
    }

    public Method setter() {
        return this.setter;
    }

    public Method getter() {
        return this.getter;
    }

	@Override
    public T get(Object object) {
        return Reflect.get(getter(), object);
    }

	@Override
    public void doSet(TypeOp<? extends T> op, String hint, Map<String, Object> object, Object value) {
        var v = op.populate(hint, object, value);
        Reflect.set(setter(), object, v);
    }

}
