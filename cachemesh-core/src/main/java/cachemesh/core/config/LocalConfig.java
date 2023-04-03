/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import cachemesh.common.config.EnumAccessor;
import cachemesh.common.config.ListAccessor;
import cachemesh.common.config.NestedAccessor;
import cachemesh.common.config.Accessor;
import cachemesh.common.config.SomeConfig;
import lombok.Setter;

@Getter
@Setter
public class LocalConfig implements SomeConfig {

    public enum Kind {
        caffeine
    }

    public static final Kind DEFAULT_KIND = Kind.caffeine;

    public static final LocalCacheConfig DEFAULT_CACHE = CaffeineConfig.DEFAULT;

    private Kind kind;

    private LocalCacheConfig defaultCache;

    private List<LocalCacheConfig> caches;

    public static final NestedAccessor<LocalCacheConfig> DEFAULT_CACHE_PROPERTY = new NestedAccessor<>(
            LocalConfig.class, "defaultCache", LocalCacheConfig.class, DEFAULT_CACHE);

    public static final Map<String, Accessor<?>> ACCESSORS = SomeConfig.buildAccessors(
            new EnumAccessor<Kind>(LocalConfig.class, "kind", Kind.class, DEFAULT_KIND), DEFAULT_CACHE_PROPERTY,
            new ListAccessor<LocalCacheConfig>(LocalConfig.class, "caches", DEFAULT_CACHE_PROPERTY,
                    new ArrayList<LocalCacheConfig>()));

    @Override
    public Collection<Accessor<?>> accessors() {
        return ACCESSORS;
    }

}
