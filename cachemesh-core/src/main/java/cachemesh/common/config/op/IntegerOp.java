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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import cachemesh.common.config.Op;

public class IntegerOp implements Op<Integer> {

    public static final IntegerOp DEFAULT = new IntegerOp();

    public static final Iterable<Class<?>> CONVERTABLES = Collections
            .unmodifiableCollection(List.of(Character.class, Number.class, String.class));

    @Override
    public Class<?> type() {
        return Integer.class;
    }

    @Override
    public Iterable<Class<?>> convertableTypes() {
        return CONVERTABLES;
    }

    @Override
    public Integer convert(String hint, Map<String, Object> parent, Object value) {
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
