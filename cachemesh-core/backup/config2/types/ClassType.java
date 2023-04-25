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
import lombok.Getter;

@Getter
public class ClassType extends AbstractType<Class<?>> {

    public static final ClassType DEFAULT = new ClassType();

    @Override
    public Class<?> klass() {
        return Class.class;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return ConfigHelper.STRING;
    }

    @Override
    public Class<?> doConvert(MapContext ctx, Path path, Object parent, Object value) {
        return ctx.resolveClass((String) value);
    }

}
