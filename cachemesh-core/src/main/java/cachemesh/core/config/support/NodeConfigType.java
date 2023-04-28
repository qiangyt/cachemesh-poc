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

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.config.types.DynamicBeanType;
import cachemesh.common.config.types.SimpleUrlType;
import cachemesh.common.err.BadValueException;
import cachemesh.common.misc.SimpleURL;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.config.TransportRegistry;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class NodeConfigType extends DynamicBeanType<NodeConfig> {

    @Nonnull
    private final TransportRegistry transportRegistry;

    public NodeConfigType(@Nonnull TransportRegistry transportRegistry, @Nonnull TypeRegistry typeRegistry) {
        super(typeRegistry, NodeConfig.class);

        checkNotNull(transportRegistry);
        this.transportRegistry = transportRegistry;
    }

    @Nonnull
    public Map<String, BeanType<? extends NodeConfig>> createMapping(@Nonnull TypeRegistry typeRegistry) {
        checkNotNull(typeRegistry);

        var r = new HashMap<String, BeanType<? extends NodeConfig>>();

        for (var entry : getTransportRegistry().getAll().entrySet()) {
            var protocol = entry.getKey();
            var provider = entry.getValue();
            r.put(protocol, provider.resolveConfigType(typeRegistry));
        }

        return r;
    }

    @Override
    @Nullable
    public Object extractKind(@Nonnull ConfigContext ctx, @Nonnull Map<String, Object> propValues) {
        checkNotNull(ctx);
        checkNotNull(propValues);

        var url = extractURL(ctx, propValues);
        return url.getProtocol();
    }

    @Nullable
    public SimpleURL extractURL(@Nonnull ConfigContext ctx, @Nonnull Map<String, Object> propValues) {
        checkNotNull(ctx);
        checkNotNull(propValues);

        if (propValues.containsKey("url") == false) {
            throw new BadValueException("%s: url is required", ctx.getPath());
        }

        SimpleURL url;

        var urlObj = propValues.get("url");
        if (urlObj instanceof SimpleURL) {
            // got cached url
            url = (SimpleURL) urlObj;
        } else {
            url = SimpleUrlType.DEFAULT.convert(ctx, urlObj);
            propValues.put("url", url); // cache it to prevent conversion again and again
        }

        return url;
    }

}
