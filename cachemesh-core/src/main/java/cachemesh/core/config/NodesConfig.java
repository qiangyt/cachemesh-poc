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

import cachemesh.common.config.NestedOp;
import cachemesh.common.config.NestedStaticOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.PropertyHelper;
import cachemesh.common.config.SomeConfig;
import cachemesh.common.config.StringOp;
import lombok.Getter;
import lombok.Setter;

public abstract class NodesConfig implements SomeConfig {

    public static final NestedOp<NodesConfig> OP = new NestedStaticOp<>(NodesConfig.class);

    public static final Property<String> KIND_PROPERTY = Property.<String> builder().config(NodesConfig.class)
            .name("kind").devault("inline").op(StringOp.DEFAULT).build();

    public static final Collection<Property<?>> PROPERTIES = PropertyHelper.buildProperties(KIND_PROPERTY);

    @Getter
    @Setter
    private String kind;

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
