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
import lombok.Getter;

import cachemesh.common.misc.LifeStage;
import cachemesh.common.misc.LogHelper;
import cachemesh.core.config.MeshConfig;

@Getter
public class MeshNetwork implements Shutdownable {

    private final Logger logger;

    private final MeshConfig config;

    private final LifeStage lifeStage;

    private final MeshCacheService cacheService;

    public MeshNetwork(MeshConfig config, MeshCacheService cacheService) {
        this.logger = LogHelper.getLogger(getClass(), config.getName());
        this.config = config;
        this.lifeStage = new LifeStage("meshnetwork", config.getName(), getLogger());
        this.cacheService = cacheService;
    }

    public <T> MeshCache<T> resolveCache(String cacheName, Class<T> valueClass) {
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

    public MeshNode findNode(String key) {
        return getCacheService().findNode(key);
    }

    public MeshNode addLocalNode(String url) throws MalformedURLException {
        return getCacheService().addLocalNode(url);
    }

    public MeshNode addRemoteNode(String url)  throws MalformedURLException {
        return getCacheService().addRemoteNode(url);
    }

}
