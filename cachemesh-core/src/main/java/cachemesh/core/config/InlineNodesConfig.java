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

import cachemesh.common.config.Prop;
import cachemesh.common.config.op.ListOp;
import cachemesh.common.config.ConfigHelper;
import lombok.Singular;

public class InlineNodesConfig extends NodesConfig {

    public static final Prop<List<NodeConfig>> INLINE_PROP = Prop.<List<NodeConfig>> builder()
            .config(InlineNodesConfig.class).name("inline").op(new ListOp<NodeConfig>(NodeConfig.OP)).build();

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(NodesConfig.PROPS, INLINE_PROP);

    @Singular("inline")
    private List<NodeConfig> inline;

}
