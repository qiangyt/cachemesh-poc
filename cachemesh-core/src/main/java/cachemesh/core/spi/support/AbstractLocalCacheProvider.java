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
package cachemesh.core.spi.support;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.Getter;

@Getter
public abstract class AbstractLocalCacheProvider<T extends LocalCache, C extends LocalCacheConfig>
        implements LocalCacheProvider {

    private final ShutdownManager shutdownManager;

    private final BeanType<C> configType;

    protected AbstractLocalCacheProvider(BeanType<C> configType, ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
        this.configType = configType;
    }

    protected abstract T doCreateCache(C config);

    @Override
    @SuppressWarnings("unchecked")
    public LocalCache createCache(LocalCacheConfig config) {
        var c = (C) config;
        return doCreateCache(c);
    }

    @Override
    public LocalCacheConfig createDefaultConfig(String name, Class<?> valueClass) {
        var r = getConfigType().convert(null, valueClass);
        r.setName(name);
        r.setValueClass(valueClass);
        return r;
    }

    @Override
    public BeanType<? extends LocalCacheConfig> resolveConfigType(TypeRegistry typeRegistry) {
        return getConfigType();
    }

}
