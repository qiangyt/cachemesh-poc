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
package cachemesh.core.config;

import java.time.Duration;
import java.util.Collection;

import cachemesh.common.config.DurationAccessor;
import cachemesh.common.config.IntegerAccessor;
import cachemesh.common.config.Accessor;
import cachemesh.common.config.SomeConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CaffeineConfig extends LocalCacheConfig {

    public static final int DEFAULT_MAXIMUM_SIZE = 100_000;

    public static final Duration DEFAULT_EXPIRE_AFTER_WIRTER = Duration.ofMinutes(5);

    public static final CaffeineConfig DEFAULT = CaffeineConfig.builder().name("default").valueClass(byte[].class)
            .serder(SerderConfig.DEFAULT).maximumSize(DEFAULT_MAXIMUM_SIZE)
            .expireAfterWrite(DEFAULT_EXPIRE_AFTER_WIRTER).build();

    private int maximumSize;

    private Duration expireAfterWrite;

    public static final Collection<Accessor<?>> ACCESSORS = SomeConfig.buildAccessors(
            new IntegerAccessor(CaffeineConfig.class, "maximumSize", DEFAULT_MAXIMUM_SIZE),
            new DurationAccessor(CaffeineConfig.class, "expireAfterWrite", DEFAULT_EXPIRE_AFTER_WIRTER));

    @Override
    public Collection<Accessor<?>> accessors() {
        return ACCESSORS;
    }

}
