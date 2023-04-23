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
package cachemesh.core.config.support;

import java.util.Map;

import cachemesh.common.config3.MapContext;
import cachemesh.common.config3.Mapper;
import cachemesh.common.config3.Path;
import cachemesh.common.config3.Type;
import cachemesh.common.config3.types.BeanType;
import cachemesh.common.config3.types.ListType;
import cachemesh.core.TransportRegistry;
import cachemesh.core.config.InlineNodesConfig;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.config.NodesConfig;
import lombok.Getter;

@Getter
public class NodesConfigMapper implements Mapper<NodesConfig> {

    private final ListType<NodeConfig> nodeConfigListType;

    public NodesConfigMapper(TransportRegistry transportRegistry) {
        Type<NodeConfig> nodeConfigType = new BeanType<>(NodeConfig.class, new NodeConfigMapper(transportRegistry));
        this.nodeConfigListType = new ListType<>(nodeConfigType);
    }

    @Override
    public NodesConfig toBean(MapContext ctx, Path path, Object parent, Map<String, Object> propValues) {
        String kind = (String) propValues.get("kind");

        NodesConfig r = null;
        if (kind.equals("inline")) {
            InlineNodesConfig c = new InlineNodesConfig();
            c.setKind("inline");

            var inlineNodes = getNodeConfigListType().convert(ctx, Path.of(path, "inline"), r,
                    propValues.get("inline"));
            c.setInline(inlineNodes);

            r = c;
        }

        return r;
    }

}
