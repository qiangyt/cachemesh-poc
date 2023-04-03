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

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import cachemesh.common.config.EnumOp;
import cachemesh.common.config.ListOp;
import cachemesh.common.config.NestedOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.Singular;

@Getter
@Setter
@Builder
public class NodesConfig implements SomeConfig {

    public static NestedOp<NodesConfig> OP = new NestedOp<>(NodesConfig.class);

    public static final Kind DEFAULT_KIND = Kind.inline;

    public enum Kind {
        inline, jgroup, k8s
    }

    @Builder.Default
    private Kind kind = DEFAULT_KIND;

    @Singular("inline")
    private List<NodeConfig> inline;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(
            Property.<Kind> builder().configClass(NodesConfig.class).propertyName("kind").defaultValue(DEFAULT_KIND)
                    .op(new EnumOp<>(Kind.class)).build(),
            Property.<List<NodeConfig>> builder().configClass(NodesConfig.class).propertyName("inline")
                    .defaultValue(new ArrayList<>()).op(new ListOp<>(NodeConfig.OP)).build());

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
