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

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import cachemesh.caffeine.CaffeineProvider;
import cachemesh.common.config.EnumOp;
import cachemesh.common.config.ListOp;
import cachemesh.common.config.NestedOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import cachemesh.core.spi.LocalCacheProvider;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
public class LocalConfig implements SomeConfig {

    public static NestedOp<LocalConfig> OP = new NestedOp<>(LocalConfig.class);

    public enum Kind {
        caffeine(CaffeineProvider.DEFAULT);

        public final LocalCacheProvider provider;

        private Kind(LocalCacheProvider provider) {
            this.provider = provider;
        }
    }

    public static final Kind DEFAULT_KIND = Kind.caffeine;

    @Builder.Default
    private Kind kind = DEFAULT_KIND;

    @Builder.Default
    private LocalCacheConfig defaultCache = CaffeineConfig.builder().name("default").valueClass(byte[].class).build();

    @Singular
    private List<LocalCacheConfig> caches;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(
            Property.<Kind> builder().configClass(LocalConfig.class).propertyName("kind").defaultValue(DEFAULT_KIND)
                    .op(new EnumOp<>(Kind.class)).build(),
            Property.<LocalCacheConfig> builder().configClass(LocalConfig.class).propertyName("defaultCache")
                    .defaultValue(CaffeineConfig.builder().name("default").valueClass(byte[].class).build())
                    .op(CaffeineConfig.OP).build(),
            Property.<List<LocalCacheConfig>> builder().configClass(LocalConfig.class).propertyName("caches")
                    .defaultValue(new ArrayList<>()).op(new ListOp<>(CaffeineConfig.OP)).build());

    public LocalConfig() {
    }

    protected LocalConfig(Kind kind, LocalCacheConfig defaultCache, List<LocalCacheConfig> caches) {
        this.kind = kind;
        this.defaultCache = defaultCache;
        this.caches = caches;
    }

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
