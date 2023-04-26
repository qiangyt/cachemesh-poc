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
package cachemesh.common.config.types;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.ConvertContext;
import cachemesh.common.config.suppport.AbstractType;
import lombok.Getter;

@Getter
public class EnumType<T extends Enum<T>> extends AbstractType<T> {

    private final Class<T> enumClass;

    public EnumType(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> getKlass() {
        return this.enumClass;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return ConfigHelper.STRING;
    }

    @Override
    protected T doConvert(ConvertContext ctx, Object value) {
        return Enum.valueOf(getEnumClass(), (String) value);
    }

}
