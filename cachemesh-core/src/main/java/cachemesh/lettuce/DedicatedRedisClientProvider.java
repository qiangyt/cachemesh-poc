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
package cachemesh.lettuce;

import java.time.Duration;

import cachemesh.common.registry.Manager;
import io.lettuce.core.RedisClient;

public class DedicatedRedisClientProvider extends Manager<LettuceConfig, RedisClient> implements RedisClientProvider {

    public static final DedicatedRedisClientProvider DEFAULT = new DedicatedRedisClientProvider();

    @Override
    protected String supplyKey(LettuceConfig config) {
        return config.getTarget();
    }

    @Override
    protected RedisClient doCreate(LettuceConfig config) {
        return RedisClient.create(config.getTarget());
    }

    @Override
    protected void doDestroy(LettuceConfig config, RedisClient client, int timeoutSeconds) throws InterruptedException {
        var timeout = Duration.ofSeconds(timeoutSeconds);
        client.shutdown(timeout, timeout);
    }

}
