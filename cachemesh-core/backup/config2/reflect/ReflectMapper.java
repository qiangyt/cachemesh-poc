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
package cachemesh.common.config2.reflect;

import java.util.Map;

import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Mapper;
import cachemesh.common.config2.Path;

public class ReflectMapper<T> implements Mapper<T> {

    private final ReflectDef<T> def;

    public ReflectMapper(ReflectDef<T> def) {
        this.def = def;
    }

    public ReflectDef<T> def() {
        return this.def;
    }

    @Override
    public T toBean(MapContext ctx, Path path, Object parent, Map<String, Object> propValues) {
        T bean = null;
        var props = def().getProps();

        for (var entry : propValues.entrySet()) {
            var pName = entry.getKey();
            var pPath = Path.of(path, pName);

            var p = props.get(pName);
            if (p == null) {
                var msg = String.format("unexpected prop: %s", pPath);
                throw new IllegalArgumentException(msg);
            }

            if (bean == null) {
                bean = def().newInstance();
            }

            var pRawValue = entry.getValue();
            var pConvertedValue = p.type().convert(ctx, pPath, parent, pRawValue);
            p.set(bean, pConvertedValue);
        }

        return bean;
    }

}
