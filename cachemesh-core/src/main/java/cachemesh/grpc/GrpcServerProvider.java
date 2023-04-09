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

import cachemesh.common.misc.Manager;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.config.GrpcNodeConfig;
import lombok.Getter;

@Getter
public class GrpcServerProvider extends Manager<GrpcNodeConfig, GrpcServer> {

    public static final GrpcServerProvider DEFAULT = new GrpcServerProvider(ShutdownManager.DEFAULT);

    private final ShutdownManager shutdownManager;

    public GrpcServerProvider(ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
    }

    @Override
    protected String supplyKey(GrpcNodeConfig config) {
        return config.getTarget();
    }

    @Override
    protected GrpcServer doCreate(GrpcNodeConfig config) {
        return new DedicatedGrpcServer(config, getShutdownManager());
    }

    @Override
    protected void doDestroy(GrpcNodeConfig config, GrpcServer server, int timeoutSeconds) throws InterruptedException {
        server.stop(timeoutSeconds);
    }

}
