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

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;

import cachemesh.common.annotation.AProperty;
import cachemesh.common.misc.Dumpable;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nonnull;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class CacheConfig implements Dumpable {

    @AProperty
    @Nonnull
    private String name;

    @AProperty
    @Nonnull
    private Class<?> valueClass;

    @Builder.Default
    @Nonnull
    private SerderConfig serder = SerderConfig.builder().build();

    protected CacheConfig() {
    }

    protected CacheConfig(@Nonnull String name, @Nonnull Class<?> valueClass, @Nonnull SerderConfig serder) {
        requireNonNull(name);
        requireNonNull(valueClass);
        requireNonNull(serder);

        this.name = name;
        this.valueClass = valueClass;
        this.serder = serder;
    }

    public abstract CacheConfig createAnother(@Nonnull String name, @Nonnull Class<?> valueClass);

    @Override
    @Nonnull public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();

        r.put("name", getName());
        r.put("valueClass", getValueClass().getCanonicalName());
        r.put("serder", getSerder().toMap());

        return r;
    }

}
