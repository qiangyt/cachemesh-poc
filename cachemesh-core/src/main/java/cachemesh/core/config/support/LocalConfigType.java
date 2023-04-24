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
package cachemesh.core.config.support;

import java.lang.reflect.Constructor;
import java.util.Map;

import cachemesh.common.config3.Path;
import cachemesh.common.config3.Prop;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.reflect.ReflectBeanType;
import cachemesh.common.config3.reflect.ReflectProp;
import cachemesh.common.config3.types.BeanType;
import cachemesh.common.misc.Reflect;
import cachemesh.core.LocalCacheManager;
import cachemesh.core.LocalCacheRegistry;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.config.LocalConfig;

public class LocalConfigType extends ReflectBeanType<LocalConfig> {

    private LocalConfigType(Class<LocalConfig> klass, Constructor<LocalConfig> ctor,
            Map<String, Prop<LocalConfig, ?>> properties) {
        super(klass, ctor, properties);
    }

    @SuppressWarnings("unchecked")
    public static LocalConfigType build(TypeRegistry typeRegistry, LocalCacheRegistry localCacheRegistry) {
        var klass = LocalConfig.class;

        var props = ReflectProp.of(typeRegistry, klass);

        var localCacheConfigType = new AbstractLocalCacheConfigType() {

            @Override
            public Object extractIndicator(Path path, Map<String, Object> propValues) {
                return null;
            }

            @Override
            public BeanType<? extends LocalCacheConfig> determineConcreteType(Object indicator) {
                var kind = (String) indicator;
                var provider = localCacheRegistry.get(kind);
                return provider.createConfig(null, getClass());
            }

        };

        var defaultCacheProp = new AbstractProp<LocalConfig, LocalCacheConfig>();

        var ctor = Reflect.defaultConstructor(klass);

        return new LocalConfigType(klass, ctor, props);
    };

}
