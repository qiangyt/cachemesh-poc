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

import java.util.HashMap;
import java.util.Map;

public class MapValue extends Value {

    MapValue(Type<?> type, Object raw) {
        super(type, raw);
    }

    @SuppressWarnings("unchecked")
    public Object get(String key) {
        Map<String, Object> map = (Map<String, Object>) get();
        return map.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Object raw) {
        var eltType = type().elementType();

        var r = new HashMap<String, Value>();
        for (var entry : ((Map<String, Object>) raw).entrySet()) {
            // TOOD: var element = entry.getValue();
            // TOOD: var elementValue = Value.of(eltType, element);
            // TOOD: r.put(entry.getKey(), elementValue);
        }

        return r;
    }

}
