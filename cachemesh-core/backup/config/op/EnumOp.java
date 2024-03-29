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

import java.util.Map;

import cachemesh.common.config.ConfigHelper;
import lombok.Getter;

@Getter
public class EnumOp<T extends Enum<T>> extends AbstractOp<T> {

    private final Class<T> enumClass;

    public EnumOp(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> klass() {
        return this.enumClass;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return ConfigHelper.STRING;
    }

    @Override
    public T convert(String hint, Map<String, Object> parent, Object value) {
        return Enum.valueOf(getEnumClass(), (String) value);
    }

}
