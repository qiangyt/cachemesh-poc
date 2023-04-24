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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.config3.Path;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.types.BeanType;
import cachemesh.common.config3.types.MappedBeanType;
import cachemesh.common.config3.types.SimpleUrlType;
import cachemesh.common.config3.types.StringType;
import cachemesh.common.misc.SimpleURL;
import cachemesh.core.TransportRegistry;
import cachemesh.core.config.NodeConfig;
import lombok.Getter;

@Getter
public class NodeConfigType extends MappedBeanType<NodeConfig> {

    public static NodeConfigType of(TypeRegistry typeRegistry) {
        return (NodeConfigType) typeRegistry.resolve(NodeConfig.class, klass -> {
            return new NodeConfigType(typeRegistry);
        });
    }

    private final TransportRegistry transportRegistry;

    public NodeConfigType(TransportRegistry transportRegistry, TypeRegistry typeRegistry) {
        super(typeRegistry, NodeConfig.class);
        this.transportRegistry = transportRegistry;
    }

    public Map<String, BeanType<? extends NodeConfig>> createMapping(TypeRegistry typeRegistry) {
        var r = new HashMap<String, BeanType<? extends NodeConfig>>();

        for (var entry: getTransportRegistry().getItems()) {
            var protocol = entry.getKey();
            var provider = entry.getValue();
            r.put(protocol, )
        }

        r.put("inline", InlineNodeConfig.of(typeRegistry));

        return r;
    }

    @Override
    public Object extractIndicator(Path path, Map<String, Object> propValues) {
        var url = extractURL(path, propValues);
        return url.getProtocol();
    }

    public SimpleURL extractURL(Path path, Map<String, Object> propValues) {
        if (propValues.containsKey("url") == false) {
            throw new IllegalArgumentException(path + ": url is required");
        }

        SimpleURL url;

        var urlObj = propValues.get("url");
        if (urlObj instanceof SimpleURL) {
            // got cached url
            url = (SimpleURL) urlObj;
        } else {
            url = SimpleUrlType.DEFAULT.convert(path, urlObj);
            propValues.put("url", url); // cache it to prevent conversion again and again
        }

        return url;
    }

}
