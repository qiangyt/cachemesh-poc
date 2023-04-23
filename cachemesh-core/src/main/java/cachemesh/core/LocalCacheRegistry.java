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

import java.util.ArrayList;
import java.util.List;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Prop;
import cachemesh.common.config.ReflectProp;
import cachemesh.common.config.op.ListOp;
import cachemesh.common.config.op.StringOp;
import cachemesh.common.misc.SimpleRegistry;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.config.LocalConfig;
import cachemesh.core.config.support.LocalCacheConfigMapper;
import cachemesh.core.spi.LocalCacheProvider;

public class LocalCacheRegistry extends SimpleRegistry<String, LocalCacheProvider> {

    public static final LocalCacheRegistry DEFAULT = new LocalCacheRegistry();

    public static final String DEFAULT_KIND = "caffeine";

    private final Iterable<Prop<?>> configProps;

    private String defaultKind = DEFAULT_KIND;

    public LocalCacheRegistry() {
        this.configProps = buildConfigProps();
    }

    public Iterable<Prop<?>> buildConfigProps() {
        var kindProp = ReflectProp.<String> builder().config(LocalConfig.class).name("kind").devault(defaultKind())
                .op(StringOp.DEFAULT).build();

        var defaultProvider = get(kindProp.devault());
        var cacheOp = new LocalCacheConfigMapper(this, StringOp.DEFAULT);

        var defaultCacheProp = ReflectProp.<LocalCacheConfig> builder().config(LocalConfig.class).name("defaultCache")
                .op(cacheOp).devault(defaultProvider.createDefaultConfig("default", byte[].class)).build();

        var cachesProp = ReflectProp.<List<LocalCacheConfig>> builder().config(LocalConfig.class).name("caches")
                .op(new ListOp<>(cacheOp)).devault(new ArrayList<>()).build();

        return ConfigHelper.props(kindProp, defaultCacheProp, cachesProp);
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
