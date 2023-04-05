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

    public static final int DEFAULT_START_TIMEOUT_SECONDS = 1;

    public static final int DEFAULT_STOP_TIMEOUT_SECONDS = 2;

    private String url;

    @Builder.Default
    private boolean local = DEFAULT_LOCAL;

    private String protocol;

    private String target;

    @Builder.Default
    private int startTimeoutSeconds = DEFAULT_START_TIMEOUT_SECONDS;

    @Builder.Default
    private int stopTimeoutSeconds = DEFAULT_STOP_TIMEOUT_SECONDS;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(
            Property.builder().configClass(NodeConfig.class).propertyName("url").op(StringOp.DEFAULT).build(),
            Property.builder().configClass(NodeConfig.class).propertyName("local").defaultValue(DEFAULT_LOCAL)
                    .op(BooleanOp.DEFAULT).build());

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
