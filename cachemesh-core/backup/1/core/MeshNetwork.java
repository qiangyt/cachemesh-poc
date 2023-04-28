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

import java.net.MalformedURLException;

import org.slf4j.Logger;

import cachemesh.common.shutdown.Shutdownable;
import cachemesh.core.cache.MeshCache;
import cachemesh.core.cache.MeshCacheService;
import cachemesh.core.config.MeshConfig;
import lombok.Getter;
import cachemesh.common.misc.LifeStage;
import cachemesh.common.misc.LogHelper;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class MeshNetwork implements Shutdownable {

    @Nonnull
    private final Logger logger;

    @Nonnull
    private final MeshConfig config;

    @Nonnull
    private final LifeStage lifeStage;

    @Nonnull
    private final MeshCacheService cacheService;

    public MeshNetwork(@Nonnull MeshConfig config, @Nonnull MeshCacheService cacheService) {
        checkNotNull(config);
        checkNotNull(cacheService);

        this.logger = LogHelper.getLogger(getClass(), config.getName());
        this.config = config;
        this.lifeStage = new LifeStage("meshnetwork", config.getName(), getLogger());
        this.cacheService = cacheService;
    }

    @Nonnull
    public <T> MeshCache<T> resolveCache(@Nonnull String cacheName, @Nonnull Class<T> valueClass) {
        checkNotNull(cacheName);
        checkNotNull(valueClass);

        return getCacheService().resolveCache(cacheName, valueClass);
    }

    public void start() throws InterruptedException {
        getLifeStage().starting();

        getCacheService().start();

        getLifeStage().started();
    }

    @Override
    public void shutdown(int timeoutSeconds) throws InterruptedException {
        getLifeStage().stopping();

        getCacheService().stop();

        getLifeStage().stopped();
    }

    @Nonnull
    public MeshNode findNode(@Nonnull String key) {
        checkNotNull(key);

        return getCacheService().findNode(key);
    }

    @Nonnull
    public MeshNode addLocalNode(@Nonnull String url) throws MalformedURLException {
        checkNotNull(url);

        return getCacheService().addLocalNode(url);
    }

    @Nonnull
    public MeshNode addRemoteNode(@Nonnull String url) throws MalformedURLException {
        checkNotNull(url);

        return getCacheService().addRemoteNode(url);
    }

}
