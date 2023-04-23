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
package cachemesh.caffeine;

import java.time.Duration;

import cachemesh.core.config.LocalCacheConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class CaffeineConfig extends LocalCacheConfig {

    public static final int DEFAULT_MAXIMUM_SIZE = 100_000;

    public static final Duration DEFAULT_EXPIRE_AFTER_WIRTER = Duration.ofMinutes(5);

    @Builder.Default
    private int maximumSize = DEFAULT_MAXIMUM_SIZE;

    @Builder.Default
    private Duration expireAfterWrite = DEFAULT_EXPIRE_AFTER_WIRTER;

    public CaffeineConfig() {
    }

    @Override
    public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
        return toBuilder().name(name).valueClass(valueClass).build();
    }

}
