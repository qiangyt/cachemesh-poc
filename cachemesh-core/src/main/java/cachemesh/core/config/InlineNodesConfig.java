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
import java.util.List;
import java.util.Map;

import cachemesh.common.config.DependingListProperty;
import cachemesh.common.config.Property;
import cachemesh.common.config.PropertyHelper;
import lombok.Singular;

public class InlineNodesConfig extends NodesConfig {

    public static final DependingListProperty<NodeConfig, String> INLINE_PROPERTY = new DependingListProperty<>(
            InlineNodesConfig.class, NodeConfig.class, "inline", NodesConfig.KIND_PROPERTY,
            Map.of(GrpcNodeConfig.PROTOCOL, GrpcNodeConfig.OP));

    public static final Collection<Property<?>> PROPERTIES = PropertyHelper.buildProperties(NodesConfig.PROPERTIES,
            INLINE_PROPERTY);

    @Singular("inline")
    private List<NodeConfig> inline;

}
