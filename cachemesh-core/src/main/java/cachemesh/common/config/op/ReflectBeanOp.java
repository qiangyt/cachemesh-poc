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
public class ReflectBeanOp<T extends Bean> extends BeanOp<T> {

    private final Constructor<T> constructor;

    public ReflectBeanOp(Class<T> type) {
        super(type);
        this.constructor = Reflect.noArgsConstructor(type);
    }

    @Override
    public T newValue(String hint, Map<String, Object> parent, Map<String, Object> value) {
        return Reflect.newInstance(getConstructor());
    }

}