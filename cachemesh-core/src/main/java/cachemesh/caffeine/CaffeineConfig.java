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

import cachemesh.common.config.Prop;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.DurationOp;
import cachemesh.common.config.op.IntegerOp;
import cachemesh.common.config.op.ReflectBeanOp;
import cachemesh.core.config.LocalCacheConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class CaffeineConfig extends LocalCacheConfig {

    public static final BeanOp<CaffeineConfig> OP = new ReflectBeanOp<>(CaffeineConfig.class);

    public static final int DEFAULT_MAXIMUM_SIZE = 100_000;

    public static final Duration DEFAULT_EXPIRE_AFTER_WIRTER = Duration.ofMinutes(5);

    public static final Prop<Integer> MAXIMUM_SIZE_PROP = Prop.<Integer> builder().config(CaffeineConfig.class)
            .name("maximumSize").devault(DEFAULT_MAXIMUM_SIZE).op(IntegerOp.DEFAULT).build();

    public static final Prop<Duration> EXPIRE_AFTER_WRITE_PROP = Prop.<Duration> builder().config(CaffeineConfig.class)
            .name("expireAfterWrite").devault(DEFAULT_EXPIRE_AFTER_WIRTER).op(DurationOp.DEFAULT).build();

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(LocalCacheConfig.PROPS, MAXIMUM_SIZE_PROP,
            EXPIRE_AFTER_WRITE_PROP);

    @Builder.Default
    private int maximumSize = DEFAULT_MAXIMUM_SIZE;

    @Builder.Default
    private Duration expireAfterWrite = DEFAULT_EXPIRE_AFTER_WIRTER;

    public CaffeineConfig() {
    }

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

    @Override
    public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
        return toBuilder().name(name).valueClass(valueClass).build();
    }

}
