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

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import cachemesh.caffeine.CaffeineProvider;
import cachemesh.common.config.DependingListProp;
import cachemesh.common.config.DependingProp;
import cachemesh.common.config.Prop;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Bean;
import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.EnumOp;
import cachemesh.common.config.op.ReflectBeanOp;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
public class LocalConfig implements Bean {

    public enum Kind {
        caffeine(CaffeineProvider.DEFAULT);

        public final LocalCacheProvider provider;

        private Kind(LocalCacheProvider provider) {
            this.provider = provider;
        }
    }

    public static BeanOp<LocalConfig> OP = new ReflectBeanOp<>(LocalConfig.class);

    public static final Kind DEFAULT_KIND = Kind.caffeine;

    public static final Prop<Kind> KIND_PROP = Prop.<Kind> builder().config(LocalConfig.class).name("kind")
            .devault(DEFAULT_KIND).op(new EnumOp<>(Kind.class)).build();

    public static final Map<Kind, ? extends BeanOp<? extends LocalCacheConfig>> CACHE_OP_MAP = Map.of(Kind.caffeine,
            CaffeineConfig.OP);

    public static final DependingProp<LocalCacheConfig, Kind> DEFAULT_CACHE_PROP = new DependingProp<>(
            LocalConfig.class, LocalCacheConfig.class, "defaultCache", KIND_PROP, CACHE_OP_MAP);

    public static final DependingListProp<LocalCacheConfig, Kind> CACHES_PROP = new DependingListProp<LocalCacheConfig, Kind>(
            LocalConfig.class, LocalCacheConfig.class, "caches", KIND_PROP, CACHE_OP_MAP);

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(KIND_PROP, DEFAULT_CACHE_PROP, CACHES_PROP);

    @Builder.Default
    private Kind kind = DEFAULT_KIND;

    @Builder.Default
    private LocalCacheConfig defaultCache = CaffeineConfig.builder().name("default").valueClass(byte[].class).build();

    @Singular("cache")
    private List<LocalCacheConfig> caches;

    public LocalConfig() {
    }

    protected LocalConfig(Kind kind, LocalCacheConfig defaultCache, List<LocalCacheConfig> caches) {
        this.kind = kind;
        this.defaultCache = defaultCache;
        this.caches = caches;
    }

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

}
