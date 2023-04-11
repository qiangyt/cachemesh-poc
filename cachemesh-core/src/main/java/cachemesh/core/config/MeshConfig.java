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

import cachemesh.common.hash.Hashing;
import cachemesh.common.hash.MurmurHash;
import cachemesh.core.TransportRegistry;
import cachemesh.core.LocalCacheRegistry;
import cachemesh.common.config.ReflectProp;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Prop;
import cachemesh.common.config.Bean;
import cachemesh.common.config.op.EnumOp;
import cachemesh.common.config.op.StringOp;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
public class MeshConfig implements Bean {

    public static enum HashingKind {
        murmur(MurmurHash.DEFAULT);

        public final Hashing instance;

        public static final EnumOp<HashingKind> OP = new EnumOp<>(HashingKind.class);

        private HashingKind(Hashing instance) {
            this.instance = instance;
        }
    }

    public static final String DEFAULT_NAME = "test";

    public static final HashingKind DEFAULT_HASHING = HashingKind.murmur;

    public static final Prop<String> NAME_PROP = ReflectProp.<String> builder().config(MeshConfig.class).name("name")
            .devault(DEFAULT_NAME).op(StringOp.DEFAULT).build();

    public static final Prop<HashingKind> HASHING_PROP = ReflectProp.<HashingKind> builder().config(MeshConfig.class)
            .name("hashing").devault(DEFAULT_HASHING).op(HashingKind.OP).build();

    public static final Prop<NodesConfig> NODES_PROP = ReflectProp.<NodesConfig> builder().config(MeshConfig.class)
            .name("nodes").devault(null).op(NodesConfig.OP).build();

    public static final Prop<LocalConfig> LOCAL_PROP = ReflectProp.<LocalConfig> builder().config(MeshConfig.class)
            .name("local").op(LocalConfig.OP).build();

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(NAME_PROP, HASHING_PROP, NODES_PROP, LOCAL_PROP);

    private String name = DEFAULT_NAME;

    private HashingKind hashing = DEFAULT_HASHING;

    private NodesConfig nodes;

    private LocalConfig local;

    private final TransportRegistry transportRegistry;

    private final LocalCacheRegistry localCacheRegistry;

    public MeshConfig(TransportRegistry transportRegistry, LocalCacheRegistry localCacheRegistry) {
        this.transportRegistry = transportRegistry;
        this.localCacheRegistry = localCacheRegistry;
    }

    @Builder
    private MeshConfig(TransportRegistry transportRegistry, LocalCacheRegistry localCacheRegistry, String name,
            HashingKind hashing, NodesConfig nodes, LocalConfig local) {
        this(transportRegistry, localCacheRegistry);

        this.name = name;
        this.hashing = hashing;
        this.nodes = nodes;
        this.local = local;
    }

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

}
