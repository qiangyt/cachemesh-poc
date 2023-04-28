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

import cachemesh.common.config.ConfigEventHub;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import cachemesh.common.shutdown.ShutdownManager;
import cachemesh.core.cache.local.LocalCacheManager;
import cachemesh.core.cache.local.LocalCacheProviderRegistry;
import cachemesh.core.cache.transport.TransportRegistry;
import cachemesh.core.config.support.LocalCacheConfigType;
import cachemesh.core.config.support.MembersConfigType;
import cachemesh.core.config.support.NodeConfigType;
import lombok.Getter;
import cachemesh.common.config.suppport.RootContext;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.config.types.ReflectBeanType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@Getter
public class MeshConfigService {

    @Nonnull
    private final ConfigEventHub eventHub;

    @Nonnull
    private final TypeRegistry typeRegistry;

    @Nonnull
    private final TransportRegistry transportRegistry;

    @Nonnull
    private final LocalCacheProviderRegistry LocalCacheProviderRegistry;

    private MeshConfig config;

    @Nonnull
    private final ClassCache classCache;

    @Nullable
    private final ShutdownManager shutdownManager;

    @Nonnull
    private final BeanType<MeshConfig> meshConfigType;

    @SuppressWarnings("unchecked")
    public MeshConfigService(@Nonnull ConfigEventHub eventHub, @Nonnull TypeRegistry parentTypeRegistry,
            @Nonnull TransportRegistry transportRegistry,
            @Nonnull LocalCacheProviderRegistry localCacheProviderRegistry, @Nonnull ClassCache classCache,
            @Nullable ShutdownManager shutdownManager) {

        checkNotNull(eventHub);
        checkNotNull(parentTypeRegistry);
        checkNotNull(transportRegistry);
        checkNotNull(localCacheProviderRegistry);
        checkNotNull(config);
        checkNotNull(classCache);

        this.eventHub = eventHub;
        this.typeRegistry = createTypeRegistry(parentTypeRegistry, localCacheProviderRegistry);
        this.transportRegistry = transportRegistry;
        this.LocalCacheProviderRegistry = localCacheProviderRegistry;
        this.classCache = classCache;
        this.shutdownManager = shutdownManager;
        this.meshConfigType = (BeanType<MeshConfig>) this.typeRegistry.load(MeshConfig.class);
    }

    @Nonnull
    public TypeRegistry createTypeRegistry(@Nonnull TypeRegistry parentTypeRegistry,
            @Nonnull LocalCacheProviderRegistry localCacheProviderRegistry) {
        checkNotNull(parentTypeRegistry);
        checkNotNull(localCacheProviderRegistry);

        var r = new TypeRegistry(parentTypeRegistry);

        r.register(MembersConfig.class, new MembersConfigType(typeRegistry));
        r.register(LocalCacheConfig.class,
                new LocalCacheConfigType(localCacheProviderRegistry, typeRegistry, Path.of("../kind")));
        r.register(LocalCacheConfig.class, new NodeConfigType(transportRegistry, typeRegistry));
        r.register(MeshConfig.class, ReflectBeanType.of(typeRegistry, MeshConfig.class));

        return r;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public MeshConfig createConfigFromYaml(@Nonnull String yamlText) {
        checkNotNull(yamlText);

        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlText);
        return createConfigFromMap(map);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public MeshConfig createConfigFromYaml(@Nonnull InputStream yamlStream) {
        checkNotNull(yamlStream);

        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlStream);
        return createConfigFromMap(map);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public MeshConfig createConfigFromYaml(@Nonnull Reader yamlReader) {
        checkNotNull(yamlReader);

        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlReader);
        return createConfigFromMap(map);
    }

    @Nonnull
    public MeshConfig createConfigFromMap(@Nonnull Map<String, Object> map) {
        checkNotNull(map);

        var r = new MeshConfig();

        var ctx = new RootContext(getClassCache(), getTypeRegistry(), map);
        getMeshConfigType().populate(ctx, r, null, map);

        return r;
    }

    @Nonnull
    public LocalCacheManager createLocalCacheManager() {
        var cfg = getConfig();
        var name = cfg.getName();

        return new LocalCacheManager(name, cfg.getLocal(), getLocalCacheProviderRegistry(), getShutdownManager());
    }

}
