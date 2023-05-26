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

import lombok.Getter;

import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

@Getter
public class ShutdownItem {

    @Nonnull
    private final ManagedShutdownable target;

    private final int timeoutSeconds;

    private volatile boolean shutdowned;

    @Nonnull
    private final ShutdownManager support;

    @Nonnull
    private final ShutdownLogger logger;

    public ShutdownItem(@Nonnull ShutdownManager support, @Nonnull ManagedShutdownable target, int timeoutSeconds) {
        this.target = requireNonNull(target);
        this.timeoutSeconds = timeoutSeconds;
        this.shutdowned = false;
        this.support = requireNonNull(support);

        if (target instanceof AbstractShutdownable) {
            this.logger = ((AbstractShutdownable) target).createShutdownLogger();
        } else {
            this.logger = new ShutdownLogger(target.getLogger());
        }
    }

    public boolean isShutdownNeeded() {
        if (this.target.isShutdownNeeded() == false) {
            return false;
        }
        return isShutdowned() == false;
    }

    public void shutdown(int timeoutSeconds) {
        this.logger.info("begin to shutdown");

        if (timeoutSeconds <= 0) {
            timeoutSeconds = getTimeoutSeconds();
        }

        try {
            this.target.onShutdown(this.logger, timeoutSeconds);
        } catch (Exception e) {
            this.logger.error(e, "got error during shutdown");
        }

        this.shutdowned = true;
        this.logger.info("end to shutdown");
    }

}
