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
package cachemesh.common.config.op;

import java.lang.reflect.Constructor;
import java.util.Map;

import cachemesh.common.config.Bean;
import cachemesh.common.misc.Reflect;
import lombok.Getter;

@Getter
public class ReflectOp<T extends Bean> extends BeanOp<T> {

	private final Class<T> type;

    private final Constructor<T> ctor;

    public ReflectOp(Class<T> type) {
        this.type = type;
        this.ctor = Reflect.defaultConstructor(type);
    }

    @Override
    public Class<?> klass() {
        return this.type;
    }

    @Override
    public T newBean(String hint, Map<String, Object> parent, Map<String, Object> value) {
        return Reflect.newInstance(getCtor());
    }

}
