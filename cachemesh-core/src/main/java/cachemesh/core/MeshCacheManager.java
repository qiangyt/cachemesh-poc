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
package cachemesh.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class MeshCacheManager {

    private final Map<String, MeshCache<?>> caches = new ConcurrentHashMap<>();

    private final LocalCacheManager nearCacheManager;

    private final MeshNetwork network;

    public MeshCacheManager(LocalCacheManager nearCacheManager, MeshNetwork network) {
        this.nearCacheManager = nearCacheManager;
        this.network = network;
    }

    @SuppressWarnings("unchecked")
    public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
        return (MeshCache<T>) this.caches.computeIfAbsent(cacheName, k -> {
            return new MeshCache<>(cacheName, getNearCacheManager(), getNetwork());
        });
    }
}
