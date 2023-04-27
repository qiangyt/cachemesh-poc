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

import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.reflect.ReflectBeanType;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.misc.ClassCache;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.LocalCacheManager;
import cachemesh.core.config.support.LocalCacheConfigType;
import cachemesh.core.config.support.MembersConfigType;
import cachemesh.core.config.support.NodeConfigType;
import lombok.Getter;
import cachemesh.common.config.suppport.RootConvertContext;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@Getter
public class MeshConfigService {

    private final TypeRegistry typeRegistry;

    private final TransportRegistry transportRegistry;

    private final LocalCacheProviderRegistry LocalCacheProviderRegistry;

    private final MeshConfig config;

    private final ClassCache classCache;

    private final ShutdownManager shutdownManager;

    private final BeanType<MeshConfig> meshConfigType;


    @SuppressWarnings("unchecked")
    public MeshConfigService(TypeRegistry parentTypeRegistry, 
                             TransportRegistry transportRegistry, 
                             LocalCacheProviderRegistry localCacheProviderRegistry,
                             MeshConfig config,
                             ClassCache classCache,
                             ShutdownManager shutdownManager) {
        this.typeRegistry = createTypeRegistry(parentTypeRegistry, localCacheProviderRegistry);
        this.transportRegistry = transportRegistry;
        this.LocalCacheProviderRegistry = localCacheProviderRegistry;
        this.config = config;
        this.classCache = classCache;
        this.shutdownManager = shutdownManager;
        this.meshConfigType = (BeanType<MeshConfig>) this.typeRegistry.load(MeshConfig.class);
    }

    public TypeRegistry createTypeRegistry(TypeRegistry parentTypeRegistry, LocalCacheProviderRegistry localCacheProviderRegistry) {
        var r = new TypeRegistry(parentTypeRegistry);

        r.register(MembersConfig.class, new MembersConfigType(typeRegistry));
        r.register(LocalCacheConfig.class,
                new LocalCacheConfigType(localCacheProviderRegistry, typeRegistry, Path.of("../kind")));
        r.register(LocalCacheConfig.class, new NodeConfigType(transportRegistry, typeRegistry));
        r.register(MeshConfig.class, ReflectBeanType.of(typeRegistry, MeshConfig.class));

        return r;
    }


    @SuppressWarnings("unchecked")
    public MeshConfig createConfigFromYaml(String yamlText) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlText);
        return createConfigFromMap(map);
    }

    @SuppressWarnings("unchecked")
    public MeshConfig createConfigFromYaml(InputStream yamlStream) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlStream);
        return createConfigFromMap(map);
    }

    @SuppressWarnings("unchecked")
    public MeshConfig createConfigFromYaml(Reader yamlReader) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlReader);
        return createConfigFromMap(map);
    }

    public MeshConfig createConfigFromMap(Map<String, Object> map) {
        var r = new MeshConfig();

        var ctx = new RootConvertContext(getClassCache(), getTypeRegistry(), map);
        getMeshConfigType().populate(ctx, r, null, map);

        return r;
    }

    public LocalCacheManager createLocalCacheManager() {
        var cfg = getConfig();
        var name = cfg.getName();

        return new LocalCacheManager(name, cfg.getLocal(), getLocalCacheProviderRegistry(),
            getShutdownManager());
    }

}
