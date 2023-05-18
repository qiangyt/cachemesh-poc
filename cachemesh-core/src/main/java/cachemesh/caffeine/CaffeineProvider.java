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
package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.ReflectBeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.bean.LocalValue;
import cachemesh.core.cache.local.AbstractLocalCacheProvider;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class CaffeineProvider extends AbstractLocalCacheProvider<CaffeineCache<?>, CaffeineConfig> {

    public CaffeineProvider(@Nonnull TypeRegistry typeRegistry, @Nullable ShutdownManager shutdownManager) {
        super(ReflectBeanType.of(typeRegistry, CaffeineConfig.class), shutdownManager);
    }

    @Override
    @Nonnull
    @SuppressWarnings("all")
    protected CaffeineCache<?> doCreateCache(@Nonnull CaffeineConfig config) {
        checkNotNull(config);

        var bldr = Caffeine.newBuilder();
        bldr.maximumSize(config.getMaximumSize()).expireAfterWrite(config.getExpireAfterWrite());

        Cache<String, LocalValue<?>> i = bldr.build();
        return new CaffeineCache(this, config, getShutdownManager(), i);
    }

}
