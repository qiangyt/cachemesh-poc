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
import cachemesh.core.LocalCacheRegistry;
import cachemesh.core.config.LocalCacheConfig;
import lombok.Getter;

@Getter
public class LocalCacheConfigMapper implements Mapper<LocalCacheConfig> {

    private final LocalCacheRegistry registry;

    public LocalCacheConfigMapper(LocalCacheRegistry registry) {
        this.registry = registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LocalCacheConfig toBean(MapContext ctx, Path path, Object parent, Map<String, Object> propValues) {
        String kind = (String) ((Map<String, Object>) parent).get("kind");
        var provider = getRegistry().get(kind);

        return provider.createConfig(ctx, path, parent, propValues);
    }

}
