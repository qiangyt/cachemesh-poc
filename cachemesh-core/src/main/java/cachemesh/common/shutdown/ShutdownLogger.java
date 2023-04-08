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

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.time.LocalDateTime;

import org.slf4j.Logger;

import cachemesh.common.misc.DateHelper;
import cachemesh.common.misc.LogHelper;
import lombok.Getter;
import lombok.Setter;

public class ShutdownLogger {

    public static final ShutdownLogger DEFAULT = new ShutdownLogger(ShutdownLogger.class, "default");

    private final Logger logger;

    @Getter
    @Setter
    private volatile boolean inShutdownHook;

    public ShutdownLogger(Class<?> klass, String name) {
        this(LogHelper.getLogger(klass, name));
    }

    public ShutdownLogger(Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return this.logger.getName();
    }

    public void info(String msgFormat, Object... args) {
        String msg = String.format(msgFormat, args);

        if (isInShutdownHook()) {
            System.out.printf("%s INFO (SHUTDOWN) - %s: %s \n", LocalDateTime.now().format(DateHelper.DAYTIME),
                    getName(), msg);
        } else {
            this.logger.info("{}: %s", kv("name", getName()), msg);
        }
    }

    public void error(Throwable cause, String msgFormat, Object... args) {
        String msg = String.format(msgFormat, args);

        if (isInShutdownHook()) {
            System.out.printf("%s ERROR (SHUTDOWN) - %s: %s \n", LocalDateTime.now().format(DateHelper.DAYTIME),
                    getName(), msg);
            if (cause != null) {
                cause.printStackTrace(System.err);
            }
        } else {
            this.logger.error("{}: %s", kv("name", getName()), msg);
        }
    }

}
