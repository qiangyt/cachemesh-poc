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

import cachemesh.common.config2.reflect.ReflectDef;
import cachemesh.common.config2.reflect.ReflectMapper;
import cachemesh.common.config2.types.ArrayType;
import cachemesh.common.config2.types.BeanType;
import cachemesh.common.config2.types.BooleanType;
import cachemesh.common.config2.types.ClassType;
import cachemesh.common.config2.types.DurationType;
import cachemesh.common.config2.types.EnumType;
import cachemesh.common.config2.types.IntegerType;
import cachemesh.common.config2.types.SimpleUrlType;
import cachemesh.common.config2.types.StringType;
import cachemesh.common.misc.SimpleManager;

public class TypeRegistry extends SimpleManager<Class<?>, Type<?>> {

    public static final TypeRegistry DEFAULT = buildDefault();

    public static TypeRegistry buildDefault() {
        var r = new TypeRegistry();

        r.register(Boolean.class, BooleanType.DEFAULT);
        r.register(Boolean.class, ClassType.DEFAULT);
        r.register(Boolean.class, DurationType.DEFAULT);
        r.register(Boolean.class, IntegerType.DEFAULT);
        r.register(Boolean.class, SimpleUrlType.DEFAULT);
        r.register(Boolean.class, StringType.DEFAULT);

        return r;
    }

    @Override
    protected String supplyKey(Class<?> klass) {
        return klass.toString();
    }

    @Override
    protected Type<?> doCreate(Class<?> klass) {
        if (klass.isArray()) {
            return new ArrayType<Object>(this, (Class<Object[]>) klass);
        }

    }

}
