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
package cachemesh.common.config2.types;

import cachemesh.common.config2.AbstractType;
import cachemesh.common.config2.ConfigHelper;
import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Path;

public class BooleanType extends AbstractType<Boolean> {

    public static final BooleanType DEFAULT = new BooleanType();

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(String.class, Character.class,
            Number.class);

    @Override
    public Class<?> klass() {
        return Boolean.class;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    public Boolean doConvert(MapContext ctx, Path path, Object parent, Object value) {
        var clazz = value.getClass();

        if (clazz == String.class) {
            var s = ((String) value).toLowerCase();
            if (s.equals("true") || s.equals("t")) {
                return Boolean.TRUE;
            }
            if (s.equals("yes") || s.equals("y")) {
                return Boolean.TRUE;
            }
            if (s.equals("ok") || s.equals("okay")) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        if (clazz == Character.class) {
            char c = ((Character) value).charValue();
            if (c == 't' || c == 'T') {
                return Boolean.TRUE;
            }
            if (c == 'y' || c == 'Y') {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        return ((Number) value).intValue() != 0;
    }

}
