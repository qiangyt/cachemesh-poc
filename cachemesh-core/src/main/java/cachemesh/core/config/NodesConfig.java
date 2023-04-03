/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.Map;
import java.util.Collection;

import cachemesh.common.config.EnumAccessor;
import cachemesh.common.config.ListAccessor;
import cachemesh.common.config.NestedAccessor;
import cachemesh.common.config.Accessor;
import cachemesh.common.config.SomeConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodesConfig implements SomeConfig {

    public static final Kind DEFAULT_KIND = Kind.inline;

    public enum Kind {
                      inline, jgroup, k8s
    }

    private Kind                                   kind;

    private List<NodeConfig>                       inline;

    public static final NestedAccessor<NodeConfig> NODE_PROPS_PROPERTY = new NestedAccessor<>(NodesConfig.class,
        NodeConfig.class, "inline", null);

    public static final Map<String, Accessor<?>>   ACCESSORS           = SomeConfig.buildAccessors(
        new EnumAccessor<Kind>(NodesConfig.class, Kind.class, "kind", Kind.inline),
        new ListAccessor<NodeConfig>(NodesConfig.class, NODE_PROPS_PROPERTY, "local", new ArrayList<NodeConfig>()));

    @Override
    public Collection<Accessor<?>> accessors() {
        return ACCESSORS;
    }

}
