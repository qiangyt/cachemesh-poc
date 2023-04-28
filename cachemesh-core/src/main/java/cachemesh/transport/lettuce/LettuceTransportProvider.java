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
package cachemesh.transport.lettuce;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.ReflectBeanType;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.MeshNode;
import cachemesh.core.spi.support.AbstractTransportProvider;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class LettuceTransportProvider extends AbstractTransportProvider<LettuceTransport, LettuceConfig> {

    @Nonnull
    private final RedisClientProvider clientProvider;

    public LettuceTransportProvider(@Nonnull TypeRegistry typeRegistry, @Nonnull RedisClientProvider clientProvider,
            @Nullable ShutdownManager shutdownManager) {
        super(ReflectBeanType.of(typeRegistry, LettuceConfig.class), shutdownManager);

        checkNotNull(clientProvider);

        this.clientProvider = clientProvider;
    }

    @Override
    public void afterNodeStop(@Nonnull MeshNode node, int timeoutSeconds) throws InterruptedException {
        checkNotNull(node);

        var tp = (LettuceTransport) node.getTransport();
        getClientProvider().destroy(tp.getConfig(), timeoutSeconds);
    }

    @Override
    @Nonnull
    protected LettuceTransport doCreateRemoteTransport(@Nonnull LettuceConfig config) {
        checkNotNull(config);

        var client = getClientProvider().resolve(config);
        return new LettuceTransport(config, client, getShutdownManager());
    }
}
