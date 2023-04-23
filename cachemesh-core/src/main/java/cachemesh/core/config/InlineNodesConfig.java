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

import cachemesh.common.config3.annotations.PropertyElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
public class InlineNodesConfig extends NodesConfig {

    @Singular("inline")
    @PropertyElement(NodeConfig.class)
    private List<NodeConfig> inline;

    @Builder
    public InlineNodesConfig(List<NodeConfig> inline) {
        setKind("inline");
        this.inline = inline;
    }

    public InlineNodesConfig() {
        setKind("inline");
    }

    @Override
    public List<NodeConfig> nodes() {
        return this.inline;
    }

}
