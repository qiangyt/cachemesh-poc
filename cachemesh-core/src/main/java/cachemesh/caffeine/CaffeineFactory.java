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
package cachemesh.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.spi.LocalCache;
import cachemesh.core.spi.LocalCacheConfig;
import cachemesh.core.spi.LocalCacheFactory;
import cachemesh.core.spi.Value;
import lombok.Getter;

@Getter
public class CaffeineFactory implements LocalCacheFactory {

    public static final CaffeineFactory DEFAULT = new CaffeineFactory(ShutdownManager.DEFAULT);

    private final ShutdownManager       shutdownManager;

    public CaffeineFactory(ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
    }

    @Override
    public LocalCache create(LocalCacheConfig config) {
        CaffeineConfig cconfig = (CaffeineConfig) config;
        Cache<String, Value> instance = Caffeine.newBuilder().maximumSize(cconfig.getMaximumSize())
            .expireAfterWrite(cconfig.getExpireAfterWrite()).build();
        return new CaffeineCache(cconfig, instance, getShutdownManager());
    }

    @Override
    public String getName() {
        return "caffeine";
    }

}
