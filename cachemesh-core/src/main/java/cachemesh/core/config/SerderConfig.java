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

import java.util.Collection;

import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.common.misc.Serderializer;
import cachemesh.common.config.EnumOp;
import cachemesh.common.config.NestedOp;
import cachemesh.common.config.NestedStaticOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class SerderConfig implements SomeConfig {

    public static enum Kind {
        jackson
    }

    public static final NestedOp<SerderConfig> OP = new NestedStaticOp<>(SerderConfig.class);

    public static final Kind DEFAULT_KIND = Kind.jackson;

    @Builder.Default
    private Kind kind = DEFAULT_KIND;

    @Builder.Default
    private Serderializer instance = JacksonSerderializer.DEFAULT;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(Property.builder()
            .config(SerderConfig.class).name("kind").devault(DEFAULT_KIND).op(new EnumOp<>(Kind.class)).build());

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
