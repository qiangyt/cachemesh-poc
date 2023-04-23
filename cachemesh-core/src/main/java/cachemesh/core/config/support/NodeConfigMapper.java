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

import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Mapper;
import cachemesh.common.config2.Path;
import cachemesh.common.config2.types.SimpleUrlType;
import cachemesh.common.misc.SimpleURL;
import cachemesh.core.TransportRegistry;
import cachemesh.core.config.NodeConfig;
import lombok.Getter;

@Getter
public class NodeConfigMapper implements Mapper<NodeConfig> {

    private final TransportRegistry registry;

    public NodeConfigMapper(TransportRegistry registry) {
        this.registry = registry;
    }

    @Override
    public NodeConfig toBean(MapContext ctx, Path path, Object parent, Map<String, Object> propValues) {
        var url = extractURL(path, propValues);
        var provider = getRegistry().get(url.getProtocol());

        return provider.createConfig(ctx, path, parent, propValues);
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
            url = SimpleUrlType.DEFAULT.convert(null, null, null, urlObj);
            propValues.put("url", url); // cache it to prevent conversion again and again
        }

        return url;
    }

}
