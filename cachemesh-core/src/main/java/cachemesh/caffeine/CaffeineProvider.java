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

import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.common.config3.Path;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.bean.Value;
import cachemesh.core.config.LocalCacheConfig;
import cachemesh.core.spi.support.AbstractLocalCacheProvider;
import lombok.Getter;

@Getter
public class CaffeineProvider extends AbstractLocalCacheProvider<CaffeineCache, CaffeineConfig> {

    public CaffeineProvider(ShutdownManager shutdownManager) {
        super(CaffeineConfig.class, shutdownManager);
    }

    @Override
    protected CaffeineCache doCreateCache(CaffeineConfig config) {
        var bldr = Caffeine.newBuilder();
        bldr.maximumSize(config.getMaximumSize()).expireAfterWrite(config.getExpireAfterWrite());

        Cache<String, Value> i = bldr.build();
        return new CaffeineCache(config, i, getShutdownManager());
    }

    @Override
    public LocalCacheConfig createDefaultConfig(String name, Class<?> valueClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDefaultConfig'");
    }

    @Override
    public LocalCacheConfig createConfig(Path path, Map<String, Object> propValues) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createConfig'");
    }

}
