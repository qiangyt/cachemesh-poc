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
package cachemesh.local.caffeine;

import java.time.Duration;
import java.util.Map;

import cachemesh.common.annotations.ADefaultDuration;
import cachemesh.common.annotations.ADefaultInt;
import cachemesh.common.misc.DurationHelper;
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

    public static final String DEFAULT_EXPIRE_AFTER_WIRTER = "5m";

    @Builder.Default
    @ADefaultInt(DEFAULT_MAXIMUM_SIZE)
    private int maximumSize = DEFAULT_MAXIMUM_SIZE;

    @Builder.Default
    @ADefaultDuration(DEFAULT_EXPIRE_AFTER_WIRTER)
    private Duration expireAfterWrite = DurationHelper.parse(DEFAULT_EXPIRE_AFTER_WIRTER);

    public CaffeineConfig() {
    }

    @Override
    public Map<String, Object> toMap() {
        var r = super.toMap();

        r.put("maximumSize", getMaximumSize());
        r.put("expireAfterWrite", getExpireAfterWrite());

        return r;
    }

}
