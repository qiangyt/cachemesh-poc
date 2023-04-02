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
package cachemesh.caffeine;

import java.time.Duration;
import java.util.Map;

import cachemesh.common.Serderializer;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.core.spi.LocalCacheConfig;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CaffeineConfig extends LocalCacheConfig {

    public static final int      DEFAULT_MAXIMUM_SIZE       = 100_000;
    private final int            maximumSize;

    public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);
    private final Duration       expireAfterWrite;

    public static CaffeineConfig defaultConfig(String name, Class<?> valueClass) {
        return defaultConfig(name, valueClass, JacksonSerderializer.DEFAULT/* , true */);
    }

    public static CaffeineConfig defaultConfig(String name, Class<?> valueClass, Serderializer serder) {
        var factory = CaffeineFactory.DEFAULT;

        return builder().name(name).valueClass(valueClass).serder(serder).factory(factory)
            .maximumSize(DEFAULT_MAXIMUM_SIZE).expireAfterWrite(DEFAULT_EXPIRE_AFTER_WRITE).build();
    }

    public CaffeineConfig(String name, Class<?> valueClass, Serderializer serder, CaffeineFactory factory,
                          int maximumSize, Duration expireAfterWrite) {
        super(name, valueClass, serder, factory);
        this.maximumSize = maximumSize;
        this.expireAfterWrite = expireAfterWrite;
    }

    @Override
    public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
        return new CaffeineConfig(name, valueClass, getSerder(), (CaffeineFactory) getFactory(), getMaximumSize(),
            getExpireAfterWrite());
    }

    @Override
    public Map<String, Object> toMap() {
        var r = super.toMap();

        r.put("maximumSize", getMaximumSize());
        r.put("expireAfterWrite", getExpireAfterWrite());

        return r;
    }

}
