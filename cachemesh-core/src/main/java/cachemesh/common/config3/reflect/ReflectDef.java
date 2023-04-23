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
package cachemesh.common.config3.reflect;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

import cachemesh.common.config3.Prop;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.misc.Reflect;
import lombok.Getter;

@Getter
public class ReflectDef<T> {

    private final Map<String, Prop<T, ?>> props;

    private final Constructor<T> ctor;

    public ReflectDef(Class<T> klass, Constructor<T> ctor, Map<String, Prop<T, ?>> props) {
        this.props = Collections.unmodifiableMap(props);
        this.ctor = Reflect.defaultConstructor(klass);
    }

    public T newInstance() {
        return Reflect.newInstance(getCtor());
    }

    public static <T> ReflectDef<T> of(TypeRegistry typeRegistry, Class<T> klass) {
        var props = ReflectProp.of(typeRegistry, klass);
        var ctor = Reflect.defaultConstructor(klass);

        return new ReflectDef<T>(klass, ctor, props);
    }

}
