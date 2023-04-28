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

import com.google.common.collect.ImmutableList;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.suppport.AbstractType;

public class IntegerType extends AbstractType<Integer> {

    public static final IntegerType DEFAULT = new IntegerType();

    public static final Iterable<Class<?>> CONVERTABLES = ImmutableList.of(Character.class, Number.class, String.class);

    @Override
    public Class<?> getKlass() {
        return Integer.class;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    protected Integer doConvert(ConfigContext ctx, Object value) {
        var clazz = value.getClass();

        if (clazz == Character.class) {
            return (int) ((Character) value).charValue();
        }
        if (clazz == String.class) {
            return Integer.valueOf((String) value);
        }

        return ((Number) value).intValue();
    }

}
