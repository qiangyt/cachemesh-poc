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

public class ListValue extends Value {

    ListValue(Type<?> type, Object raw) {
        super(type, raw);
    }

    @SuppressWarnings("unchecked")
    public Object get(int index) {
        var list = (List<Object>) get();
        return list.get(index);
    }

    @Override
    public Object convert(Object raw) {
        int capacity;
        if (raw instanceof Collection) {
            capacity = ((Collection<?>) raw).size();
        } else if (raw.getClass().isArray()) {
            capacity = Array.getLength(raw);
        } else {
            capacity = 8;
        }

        var eltType = type().elementType();

        var r = new ArrayList<Object>(capacity);
        for (var element : (Iterable<?>) raw) {
            var elementValue = Value.of(eltType, element);
            r.add(elementValue);
        }

        return r;
    }

}
