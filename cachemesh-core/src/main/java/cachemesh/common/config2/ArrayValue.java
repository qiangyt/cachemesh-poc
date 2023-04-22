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
package cachemesh.common.config2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayValue extends Value {

    ArrayValue(Type<?> type, Object raw) {
        super(type, raw);
    }

    @SuppressWarnings("unchecked")
    public Object get(int index) {
        var list = (List<Object>) get();
        return list.get(index);
    }

    @Override
    public Object convert(Object raw) {
        int length = Array.getLength(raw);
        var eltType = type().elementType();

        var r = Array.newInstance(type().elementType().klass(), length);
        for (int i = 0; i < length; i++) {
            var eltValue = Value.of(eltType, element);
            r.add(eltementValue);
        }

        return r;
    }

}
