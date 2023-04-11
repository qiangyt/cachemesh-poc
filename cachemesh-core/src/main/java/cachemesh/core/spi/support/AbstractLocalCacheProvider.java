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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.ReflectOp;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.Getter;

public abstract class AbstractLocalCacheProvider<T extends LocalCache, C extends LocalCacheConfig>
        implements LocalCacheProvider {

    private final BeanOp<C> configOp;

    @Getter
    private final ShutdownManager shutdownManager;

    protected AbstractLocalCacheProvider(Class<C> configClass, ShutdownManager shutdownManager) {
        this.configOp = new ReflectOp<>(configClass);
        this.shutdownManager = shutdownManager;
    }

    @Override
    public LocalCacheConfig createDefaultConfig(String name, Class<?> valueClass) {
        return doCreateDefaultConfig(name, valueClass);
    }

    protected abstract T doCreateCache(C config);

    protected C doCreateConfig(String hint, Map<String, Object> parent, Map<String, Object> value) {
        return this.configOp.populate(hint, parent, value);
    }

    protected C doCreateDefaultConfig(String name, Class<?> valueClass) {
        var map = new HashMap<String, Object>();
        return doCreateConfig("defaultCache", null, map);
    }

    @Override
    public LocalCacheConfig createConfig(String hint, Map<String, Object> parent, Map<String, Object> value) {
        return doCreateConfig(hint, parent, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public LocalCache createCache(LocalCacheConfig config) {
        var c = (C) config;
        return doCreateCache(c);
    }

}
