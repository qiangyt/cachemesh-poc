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
package cachemesh.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.DependingListProp;
import cachemesh.common.config.DependingProp;
import cachemesh.common.config.Prop;
import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.StringOp;
import cachemesh.common.misc.SimpleRegistry;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.config.LocalConfig;
import cachemesh.core.spi.LocalCacheProvider;

public class LocalCacheRegistry extends SimpleRegistry<String, LocalCacheProvider> {

    public static final LocalCacheRegistry DEFAULT = new LocalCacheRegistry();

    public static final String DEFAULT_KIND = "caffeine";

    private final Map<String, BeanOp<? extends LocalCacheConfig>> configOpMap = new ConcurrentHashMap<>();

    private final Iterable<Prop<?>> configProps;

    private String defaultKind = "caffeine";

    public LocalCacheRegistry() {
        var kindProp = Prop.<String> builder().config(LocalConfig.class).name("kind").devault(DEFAULT_KIND)
                .op(StringOp.DEFAULT).build();
        var defaultCacheProp = new DependingProp<>(LocalConfig.class, LocalCacheConfig.class, "defaultCache", kindProp,
                this.configOpMap);
        var cachesProp = new DependingListProp<LocalCacheConfig, String>(LocalConfig.class, LocalCacheConfig.class,
                "caches", kindProp, this.configOpMap);
        this.configProps = ConfigHelper.props(kindProp, defaultCacheProp, cachesProp);
    }

    @Override
    protected LocalCacheProvider supplyItem(String kind, LocalCacheProvider provider) {
        this.configOpMap.put(kind, provider.configOp());
        return provider;
    }

    @Override
    protected String supplyKey(String kind) {
        return kind;
    }

    public Iterable<Prop<?>> configProps() {
        return this.configProps;
    }

    public String defaultKind() {
        return this.defaultKind;
    }

}
