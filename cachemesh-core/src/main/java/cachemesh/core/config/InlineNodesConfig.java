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

import java.util.List;

import cachemesh.common.config.ReflectProp;
import cachemesh.common.config.op.ListOp;
import cachemesh.core.TransportRegistry;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Prop;
import lombok.Singular;

public class InlineNodesConfig extends NodesConfig {
    @Singular("inline")
    private List<NodeConfig> inline;

    protected InlineNodesConfig(TransportRegistry registry) {
        super(registry);
    }

    @Override
    public Iterable<Prop<?>> props() {
        var inlinesProp = ReflectProp.<List<NodeConfig>> builder().config(InlineNodesConfig.class).name("inline")
                .op(new ListOp<>(new NodeConfigOp(getRegistry()))).build();

        return ConfigHelper.props(NodesConfig.PROPS, inlinesProp);
        // return getRegistry().configProps();
    }

}
