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

import java.util.Map;

public class Value {

    private final Type<?> type;

    private Object converted;

    private final Object raw;

    protected Value(Type<?> type, Object raw) {
        this.type = type;
        this.raw = raw;
    }

    public Type<?> type() {
        return this.type;
    }

    public Object get() {
        if (this.converted != null) {
            return this.converted;
        }
        return this.converted = convert(raw());
    }

    public Object raw() {
        return this.raw;
    }

    public Object convert(Object raw) {
        return this.raw;
    }

    static Value of(TypeRegistry typeRegistry, Object raw) {
        var klass = raw.getClass();

        if (klass.isArray()) {
            var eltType = typeRegistry.get(klass.getComponentType());
            return new ArrayValue(eltType, raw);
        }
        if (raw instanceof Map) {
            return null;//TODO: return new MapValue(type, raw);
        }
        return null;//TODO: return new Value(type, raw);
    }

}
