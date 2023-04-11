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

import java.util.Map;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.TypeOp;
import cachemesh.core.LocalCacheRegistry;
import lombok.Getter;

@Getter
public class LocalCacheConfigOp implements TypeOp<LocalCacheConfig> {

    private final LocalCacheRegistry registry;

    private final TypeOp<String> kindOp;

    public LocalCacheConfigOp(LocalCacheRegistry registry, TypeOp<String> kindOp) {
        this.registry = registry;
        this.kindOp = kindOp;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return ConfigHelper.MAP;
    }

    @Override
    public Class<?> klass() {
        return LocalCacheConfig.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LocalCacheConfig convert(String hint, Map<String, Object> parent, Object value) {
        var kind = getKindOp().populate(hint, null, parent);
        var provider = getRegistry().get(kind);

        var map = (Map<String, Object>) value;
        return provider.createConfig(hint, parent, map);
    }

}
