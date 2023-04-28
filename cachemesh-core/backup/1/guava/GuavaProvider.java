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
package cachemesh.guava;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.ReflectBeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.bean.Value;
import cachemesh.core.cache.local.AbstractLocalCacheProvider;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static com.google.common.base.Preconditions.*;

@Getter
public class GuavaProvider extends AbstractLocalCacheProvider<GuavaCache, GuavaConfig> {

    public GuavaProvider(@Nonnull TypeRegistry typeRegistry, @Nullable ShutdownManager shutdownManager) {
        super(ReflectBeanType.of(typeRegistry, GuavaConfig.class), shutdownManager);
    }

    @Override
    @Nonnull
    protected GuavaCache doCreateCache(@Nonnull GuavaConfig config) {
        checkNotNull(config);

        var bldr = CacheBuilder.newBuilder();
        bldr.maximumSize(config.getMaximumSize()).expireAfterWrite(config.getExpireAfterWrite());

        Cache<String, Value> i = bldr.build();
        return new GuavaCache(config, i, getShutdownManager());
    }

}
