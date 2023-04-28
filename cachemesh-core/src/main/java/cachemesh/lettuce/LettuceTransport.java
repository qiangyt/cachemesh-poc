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

import io.lettuce.core.api.sync.RedisCommands;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.bean.GetResult;
import cachemesh.core.spi.Transport;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class LettuceTransport extends AbstractShutdownable implements Transport {

    @Nonnull
    private StatefulRedisConnection<String, byte[]> connection;

    @Nonnull
    private final RedisClient client;

    @Nonnull
    private final LettuceConfig config;

    public LettuceTransport(@Nonnull LettuceConfig config, @Nonnull RedisClient client,
            @Nullable ShutdownManager shutdownManager) {
        super(config.getTarget(), shutdownManager);

        checkNotNull(client);

        this.config = config;
        this.client = client;
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public void start(int timeoutSeconds) throws InterruptedException {
        this.connection = getClient().connect(LettuceCodec.DEFAULT);
    }

    @Override
    public void stop(int timeoutSeconds) throws InterruptedException {
        shutdown(timeoutSeconds);
    }

    @Override
    public void onShutdown(@Nonnull ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        checkNotNull(shutdownLogger);

        var conn = getConnection();
        if (conn != null) {
            conn.close();
        }
    }

    public @Nonnull String generateRedisKey(@Nonnull String cacheName, @Nonnull String key) {
        checkNotNull(cacheName);
        checkNotNull(key);

        var sep = getConfig().getKeySeparator();

        return new StringBuilder(cacheName.length() + sep.length() + key.length()).append(cacheName).append(sep)
                .append(key).toString();
    }

    @Nonnull
    RedisCommands<String, byte[]> syncCommand() {
        return getConnection().sync();
    }

    @Override
    @Nonnull
    public GetResult<byte[]> getSingle(@Nonnull String cacheName, @Nonnull String key, long version) {
        checkNotNull(cacheName);
        checkNotNull(key);

        var redisKey = generateRedisKey(cacheName, key);

        var cmds = syncCommand();
        var value = cmds.get(redisKey);
        if (value == null) {// TODO: how to indicate we do have the value but the value is null
            return GetResult.notFound();
        }

        return GetResult.ok(value, 0);
    }

    @Override
    public long putSingle(@Nonnull String cacheName, @Nonnull String key, byte[] value) {
        checkNotNull(cacheName);
        checkNotNull(key);

        var redisKey = generateRedisKey(cacheName, key);

        var cmds = syncCommand();
        cmds.set(redisKey, value);

        return 0;
    }

}
