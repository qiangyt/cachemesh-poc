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
package cachemesh.common.config;

import cachemesh.common.config.types.BooleanType;
import cachemesh.common.config.types.ClassType;
import cachemesh.common.config.types.DurationType;
import cachemesh.common.config.types.IntegerType;
import cachemesh.common.config.types.SimpleUrlType;
import cachemesh.common.config.types.StringType;
import cachemesh.common.registry.Registry;

import javax.annotation.Nonnull;

public class TypeRegistry extends Registry<Class<?>, Type<?>> {

    public static final TypeRegistry DEFAULT = new TypeRegistry(null);
    static {
        var r = DEFAULT;

        r.register(Boolean.class, BooleanType.DEFAULT);
        r.register(Boolean.class, ClassType.DEFAULT);
        r.register(Boolean.class, DurationType.DEFAULT);
        r.register(Boolean.class, IntegerType.DEFAULT);
        r.register(Boolean.class, SimpleUrlType.DEFAULT);
        r.register(Boolean.class, StringType.DEFAULT);
    }

    public TypeRegistry(@Nonnull TypeRegistry parent) {
        super(parent);
    }

    @Override
    public String getValueName() {
        return "type";
    }

}
