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

import java.util.Map;

import cachemesh.common.config2.ConfigHelper;
import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.AbstractType;
import cachemesh.common.config2.Mapper;
import cachemesh.common.config2.Path;

public class BeanType<T> extends AbstractType<T> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Map.class);

    private final Class<T> klass;

    private final Mapper<T> mapper;

    public BeanType(Class<T> klass, Mapper<T> mapper) {
        this.klass = klass;
        this.mapper = mapper;
    }

    @Override
    public Class<?> klass() {
        return this.klass;
    }

    public Mapper<T> mapper() {
        return this.mapper;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T doConvert(MapContext ctx, Path path, Object parent, Object value) {
        var propValues = (Map<String, Object>) value;
        return mapper().toBean(ctx, path, parent, propValues);
    }

}
