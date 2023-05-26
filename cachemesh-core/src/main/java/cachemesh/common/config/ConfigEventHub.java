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
package cachemesh.common.config;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import lombok.Getter;

@Getter
public class ConfigEventHub {// implements SubscriberExceptionHandler {

    private final Logger log;

    @Nonnull
    private final EventBus bus;

    public ConfigEventHub() {
        this(new EventBus("cache config"));
    }

    public ConfigEventHub(@Nonnull EventBus bus) {
        requireNonNull(bus);

        this.bus = bus;
        this.log = LoggerFactory.getLogger(getBus().identifier());
    }

    public void register(@Nonnull ConfigEventListener listener) {
        requireNonNull(listener);

        getBus().register(listener);
    }

    public void unregister(@Nonnull ConfigEventListener listener) {
        requireNonNull(listener);

        getBus().unregister(listener);
    }

    public <T> void post(@Nonnull ConfigEvent<T> event) {
        requireNonNull(event);

        getBus().unregister(event);
    }

}
