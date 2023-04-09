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

import cachemesh.common.config.Bean;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Op;
import lombok.Getter;

@Getter
public abstract class BeanOp<T extends Bean> implements Op<T> {

    private final Class<T> type;

    public BeanOp(Class<T> type) {
        this.type = type;
    }

    public abstract T newValue(String hint, Map<String, Object> parent, Map<String, Object> value);

    @Override
    public Class<?> type() {
        return this.type;
    }

    @Override
    public Iterable<Class<?>> convertableTypes() {
        return ConfigHelper.MAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convert(String hint, Map<String, Object> parent, Object value) {
        var map = (Map<String, Object>) value;
        T r = newValue(hint, parent, map);
        r.withMap(hint, parent, map);
        return r;
    }

}
