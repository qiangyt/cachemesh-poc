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

import java.util.Collection;

import cachemesh.common.config.BooleanOp;
import cachemesh.common.config.NestedOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import cachemesh.common.config.StringOp;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class NodeConfig implements SomeConfig {

    public static final NestedOp<NodeConfig> OP = new NestedOp<>(NodeConfig.class);

    public static final boolean DEFAULT_LOCAL = false;

    private String url;

    @Builder.Default
    private boolean local = DEFAULT_LOCAL;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(
            Property.<String> builder().configClass(NodeConfig.class).propertyName("url").op(StringOp.DEFAULT).build(),
            Property.<Boolean> builder().configClass(NodeConfig.class).propertyName("local").defaultValue(DEFAULT_LOCAL)
                    .op(BooleanOp.DEFAULT).build());

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
