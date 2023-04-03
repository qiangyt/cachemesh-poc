/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.HasName;
import cachemesh.common.util.DateHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ShutdownLogger implements HasName {

    public static final ShutdownLogger DEFAULT = new ShutdownLogger(ShutdownLogger.class);

    private final Logger logger;

    @Setter
    private volatile boolean inShutdownHook;

    public ShutdownLogger(Class<?> klass) {
        this(klass.getSimpleName());
    }

    public ShutdownLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    public ShutdownLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();
        r.put("name", getName());
        return r;
    }

    public void info(String msgFormat, Object... args) {
        String msg = String.format(msgFormat, args);

        if (isInShutdownHook()) {
            System.out.printf("%s INFO (SHUTDOWN) - %s: %s \n", LocalDateTime.now().format(DateHelper.DAYTIME),
                    getName(), msg);
        } else {
            logger.info("{}: %s", kv("name", getName()), msg);
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
            logger.error("{}: %s", kv("name", getName()), msg);
        }
    }

    @Override
    public String getName() {
        return this.logger.getName();
    }

}
