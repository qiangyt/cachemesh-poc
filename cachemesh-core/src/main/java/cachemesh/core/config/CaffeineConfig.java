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
package cachemesh.core.config;

import java.time.Duration;
import java.util.Collection;

import cachemesh.common.config.DurationOp;
import cachemesh.common.config.IntegerOp;
import cachemesh.common.config.NestedOp;
import cachemesh.common.config.NestedStaticOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class CaffeineConfig extends LocalCacheConfig {

    public static final NestedOp<CaffeineConfig> OP = new NestedStaticOp<>(CaffeineConfig.class);

    public static final int DEFAULT_MAXIMUM_SIZE = 100_000;

    public static final Duration DEFAULT_EXPIRE_AFTER_WIRTER = Duration.ofMinutes(5);

    @Builder.Default
    private int maximumSize = DEFAULT_MAXIMUM_SIZE;

    @Builder.Default
    private Duration expireAfterWrite = DEFAULT_EXPIRE_AFTER_WIRTER;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(LocalCacheConfig.PROPERTIES,
            Property.builder().config(CaffeineConfig.class).name("maximumSize").devault(DEFAULT_MAXIMUM_SIZE)
                    .op(IntegerOp.DEFAULT).build(),
            Property.builder().config(CaffeineConfig.class).name("expireAfterWrite")
                    .devault(DEFAULT_EXPIRE_AFTER_WIRTER).op(DurationOp.DEFAULT).build());

    public CaffeineConfig() {
    }

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

    @Override
    public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
        return toBuilder().name(name).valueClass(valueClass).build();
    }

}
