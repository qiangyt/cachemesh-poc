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
package cachemesh.core.config;

import lombok.Getter;

import java.util.List;

import cachemesh.common.config.Prop;
import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.ReflectBeanOp;
import cachemesh.common.config.Bean;
import cachemesh.core.LocalCacheRegistry;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
public class LocalConfig implements Bean {

    public static BeanOp<LocalConfig> OP = new ReflectBeanOp<>(LocalConfig.class);

    private String kind;

    private LocalCacheConfig defaultCache;

    @Singular("cache")
    private List<LocalCacheConfig> caches;

    private final LocalCacheRegistry registry;

    protected LocalConfig() {
        this(LocalCacheRegistry.DEFAULT);
    }

    protected LocalConfig(LocalCacheRegistry registry) {
        this.registry = registry;
        this.kind = registry.defaultKind();
        this.defaultCache = registry.get(this.kind).createDefaultConfig("default", byte[].class);
    }

    protected LocalConfig(LocalCacheConfig defaultCache, List<LocalCacheConfig> caches) {
        this(LocalCacheRegistry.DEFAULT, defaultCache, caches);
    }

    protected LocalConfig(LocalCacheRegistry registry, LocalCacheConfig defaultCache, List<LocalCacheConfig> caches) {
        this.registry = registry;
        this.kind = defaultCache.getName();
        this.defaultCache = defaultCache;
        this.caches = caches;
    }

    public LocalCacheProvider getCacheProvider() {
        return getRegistry().get(getKind());
    }

    @Override
    public Iterable<Prop<?>> props() {
        return getRegistry().configProps();
    }

}
