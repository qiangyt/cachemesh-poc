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
package cachemesh.common.shutdown;

import org.slf4j.Logger;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.LogHelper;
import lombok.Getter;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

@Getter
public abstract class AbstractShutdownable implements ManagedShutdownable {

    @Nonnull
    private final Logger logger;

    @Nonnull
    private final String name;

    @Nonnull
    private final ShutdownManager shutdownManager;

    protected AbstractShutdownable(@Nonnull String name, @Nullable ShutdownManager shutdownManager) {
        this(name, shutdownManager, 0);
    }

    protected AbstractShutdownable(@Nonnull String name, @Nullable ShutdownManager shutdownManager,
            int shutdownTimeoutSeconds) {
        this.name = requireNonNull(name);
        this.logger = LogHelper.getLogger(getClass(), name);

        this.shutdownManager = shutdownManager;
        if (shutdownManager != null) {
            shutdownManager.register(this, shutdownTimeoutSeconds);
        }
    }

    @Override
    public void shutdown(int timeoutSeconds) throws InterruptedException {
        if (isShutdownNeeded() == false) {
            throw new BadStateException("%s doesn't need shutdown", getName());
        }

        var sd = getShutdownManager();
        if (sd != null) {
            sd.shutdown(this, timeoutSeconds);
        } else {
            onShutdown(createShutdownLogger(), timeoutSeconds);
        }
    }

    @Nonnull
    public ShutdownLogger createShutdownLogger() {
        return new ShutdownLogger(getLogger());
    }

}
