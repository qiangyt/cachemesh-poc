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

import cachemesh.core.TransportRegistry;
import cachemesh.core.config.support.AbstractLocalCacheConfigType;
import cachemesh.core.config.support.MembersConfigType;
import lombok.Getter;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.types.BeanType;
import cachemesh.core.LocalCacheRegistry;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@Getter
public class MeshConfigFactory {

    private final TransportRegistry transportRegistry;

    private final LocalCacheRegistry localCacheRegistry;

    private final TypeRegistry typeRegistry;

    public MeshConfigFactory() {
        this(TransportRegistry.DEFAULT, LocalCacheRegistry.DEFAULT, TypeRegistry.DEFAULT);
    }

    public MeshConfigFactory(TransportRegistry transportRegistry, LocalCacheRegistry localCacheRegistry,
            TypeRegistry typeRegistry) {
        this.transportRegistry = transportRegistry;
        this.localCacheRegistry = localCacheRegistry;
        this.typeRegistry = typeRegistry;

        typeRegistry.register(MembersConfig.class, new MembersConfigType(typeRegistry));
        typeRegistry.register(LocalCacheConfig.class, new AbstractLocalCacheConfigType() {

            @Override
            public BeanType<? extends LocalCacheConfig> determineConcreteType(Object indicator) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'determineConcreteType'");
            }

        });

    }

    @SuppressWarnings("unchecked")
    public MeshConfig fromYaml(String yamlText) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlText);
        return fromMap(map);
    }

    @SuppressWarnings("unchecked")
    public MeshConfig fromYaml(InputStream yamlStream) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlStream);
        return fromMap(map);
    }

    @SuppressWarnings("unchecked")
    public MeshConfig fromYaml(Reader yamlReader) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlReader);
        return fromMap(map);
    }

    public MeshConfig fromMap(Map<String, Object> map) {
        var r = new MeshConfig(getTransportRegistry(), getLocalCacheRegistry());
        r.withMap("", null, map);
        return r;
    }

}
