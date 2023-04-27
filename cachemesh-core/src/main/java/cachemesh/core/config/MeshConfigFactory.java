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
import cachemesh.core.config.support.LocalCacheConfigType;
import cachemesh.core.config.support.MembersConfigType;
import cachemesh.core.config.support.NodeConfigType;
import lombok.Getter;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.reflect.ReflectBeanType;
import cachemesh.common.config.suppport.RootConvertContext;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.misc.ClassCache;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.LocalCacheManager;
import cachemesh.core.LocalCacheProviderRegistry;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@Getter
public class MeshConfigFactory {

    private final TransportRegistry transportRegistry;

    private final LocalCacheProviderRegistry localCacheProviderRegistry;

    private final TypeRegistry typeRegistry;

    private final ClassCache classCache;

    private final BeanType<MeshConfig> meshConfigType;

    private final ShutdownManager shutdownManager;

    public MeshConfigFactory() {
        this(TransportRegistry.DEFAULT, LocalCacheProviderRegistry.DEFAULT, ClassCache.DEFAULT, TypeRegistry.DEFAULT,
                ShutdownManager.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    public MeshConfigFactory(TransportRegistry transportRegistry, LocalCacheProviderRegistry localCacheProviderRegistry,
            ClassCache classCache, TypeRegistry parentTypeRegistry, ShutdownManager shutdownManager) {
        this.transportRegistry = transportRegistry;
        this.localCacheProviderRegistry = localCacheProviderRegistry;
        this.classCache = classCache;
        this.shutdownManager = shutdownManager;

        var typeRegistry = new TypeRegistry(parentTypeRegistry);

        typeRegistry.register(MembersConfig.class, new MembersConfigType(typeRegistry));
        typeRegistry.register(LocalCacheConfig.class,
                new LocalCacheConfigType(localCacheProviderRegistry, typeRegistry, Path.of("../kind")));
        typeRegistry.register(LocalCacheConfig.class, new NodeConfigType(transportRegistry, typeRegistry));
        typeRegistry.register(MeshConfig.class, ReflectBeanType.of(typeRegistry, MeshConfig.class));

        this.typeRegistry = typeRegistry;

        this.meshConfigType = (BeanType<MeshConfig>) typeRegistry.load(MeshConfig.class);
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
        var r = new MeshConfig(getTransportRegistry(), getLocalCacheProviderRegistry());

        var ctx = new RootConvertContext(getClassCache(), getTypeRegistry(), map);
        getMeshConfigType().populate(ctx, r, null, map);

        var localConfig = r.getLocal();
        var localCacheManager = new LocalCacheManager(r.getName(), localConfig, r.getLocalCacheProviderRegistry(),
                getShutdownManager());
        r.setLocalCacheManager(localCacheManager);

        r.setNearCacheManager(localCacheManager);

        return r;
    }

}
