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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.config.types.KindPathingDynamicBeanType;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.config.LocalCacheProviderRegistry;
import cachemesh.core.config.LocalConfig;
import lombok.Getter;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class LocalCacheConfigType extends KindPathingDynamicBeanType<LocalCacheConfig> {

    @Nonnull
    private final LocalCacheProviderRegistry localCacheRegistry;

    public LocalCacheConfigType(@Nonnull LocalCacheProviderRegistry localCacheRegistry,
            @Nonnull TypeRegistry typeRegistry, @Nonnull Path path) {
        super(typeRegistry, LocalCacheConfig.class, path, LocalConfig.DEFAULT_KIND);

        checkNotNull(localCacheRegistry);
        this.localCacheRegistry = localCacheRegistry;
    }

    @Nonnull
    public Map<String, BeanType<? extends LocalCacheConfig>> createMapping(@Nonnull TypeRegistry typeRegistry) {
        checkNotNull(typeRegistry);

        var r = new HashMap<String, BeanType<? extends LocalCacheConfig>>();

        for (var entry : getLocalCacheRegistry().getAll().entrySet()) {
            var protocol = entry.getKey();
            var provider = entry.getValue();
            r.put(protocol, provider.resolveConfigType(typeRegistry));
        }

        return r;
    }

}
