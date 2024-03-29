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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.LifeStage;
import cachemesh.common.shutdown.AbstractShutdownable;
import cachemesh.common.shutdown.ShutdownLogger;
import cachemesh.common.shutdown.ShutdownManager;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class DedicatedGrpcServer extends AbstractShutdownable implements GrpcServer {

    @Nonnull
    private final ServerBuilder<?> builder;

    @Setter(AccessLevel.PROTECTED)
    @Nonnull
    private Server instance;

    @Nonnull
    private final GrpcConfig config;

    @Nonnull
    private final LifeStage lifeStage;

    public DedicatedGrpcServer(@Nonnull GrpcConfig config, @Nullable ShutdownManager shutdownManager) {
        super(config.getTarget(), shutdownManager);

        checkNotNull(config);

        this.config = config;

        this.builder = Grpc.newServerBuilderForPort(config.getPort(), InsecureServerCredentials.create());
        this.lifeStage = new LifeStage("grpc-server", config.getTarget(), getLogger());
    }

    @Override
    public boolean isStarted() {
        return getLifeStage().getStage() == LifeStage.Stage.started;
    }

    @Override
    public boolean isShutdownNeeded() {
        return isStarted();
    }

    @Override
    public void addService(@Nonnull BindableService service) {
        checkNotNull(service);

        if (isStarted()) {
            throw new BadStateException("%s: cannot add service any more once server started", getName());
        }
        getBuilder().addService(service);
    }

    @Override
    public void stop(int timeoutSeconds) throws InterruptedException {
        shutdown(timeoutSeconds);
    }

    public void start(int timeoutSeconds) {
        getLifeStage().starting();

        var inst = getBuilder().build();

        try {
            inst.start();
            setInstance(inst);

            getLifeStage().started();
        } catch (IOException e) {
            throw new BadStateException(e, "%s: failed to start server", getName());
        }
    }

    @Override
    public void onShutdown(@Nonnull ShutdownLogger shutdownLogger, int timeoutSeconds) throws InterruptedException {
        checkNotNull(shutdownLogger);

        getLifeStage().stopping();

        var inst = getInstance();
        setInstance(null);

        inst.shutdown();
        inst.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);

        getLifeStage().stopped();
    }

}
