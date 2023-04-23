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
import cachemesh.common.config3.annotations.Property;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class LocalCacheConfig {

    @Property
    private String name;

    @Property
    private Class<?> valueClass;

    @Builder.Default
    private SerderConfig serder = SerderConfig.builder().build();

    protected LocalCacheConfig() {
    }

    protected LocalCacheConfig(String name, Class<?> valueClass, SerderConfig serder) {
        this.name = name;
        this.valueClass = valueClass;
        this.serder = serder;
    }

    public abstract LocalCacheConfig buildAnother(String name, Class<?> valueClass);

}
