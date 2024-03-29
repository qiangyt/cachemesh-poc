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
package cachemesh.grpc;

import cachemesh.common.registry.Manager;
import cachemesh.common.shutdown.ShutdownManager;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class GrpcServerProvider extends Manager<GrpcConfig, GrpcServer> {

    public static final GrpcServerProvider DEFAULT = new GrpcServerProvider(ShutdownManager.DEFAULT);

    @Nullable
    private final ShutdownManager shutdownManager;

    public GrpcServerProvider(@Nullable ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
    }

    @Override
    @Nonnull
    protected GrpcServer doCreate(@Nonnull GrpcConfig config) {
        checkNotNull(config);
        return new DedicatedGrpcServer(config, getShutdownManager());
    }

    @Override
    protected void doDestroy(@Nonnull GrpcConfig config, @Nonnull GrpcServer server, int timeoutSeconds)
            throws InterruptedException {
        checkNotNull(config);
        checkNotNull(server);

        server.stop(timeoutSeconds);
    }

    @Override
    @Nonnull
    public String getValueName() {
        return "grpc server";
    }

}
