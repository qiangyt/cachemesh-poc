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

import cachemesh.common.HasName;
import cachemesh.common.hash.Hashing;
import cachemesh.common.hash.MurmurHash;
import cachemesh.common.config.EnumOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import cachemesh.common.config.StringOp;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@Getter
@Setter
@Builder
public class MeshConfig implements SomeConfig, HasName {

    public static enum HashingKind {
        murmur(MurmurHash.DEFAULT);

        public final Hashing instance;

        private HashingKind(Hashing instance) {
            this.instance = instance;
        }
    }

    public static final String DEFAULT_NAME = "test";

    public static final HashingKind DEFAULT_HASHING = HashingKind.murmur;

    @Builder.Default
    private String name = DEFAULT_NAME;

    @Builder.Default
    private HashingKind hashing = DEFAULT_HASHING;

    @Builder.Default
    private NodesConfig nodes = NodesConfig.builder().build();

    @Builder.Default
    private LocalConfig local = LocalConfig.builder().build();

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(
            Property.builder().config(MeshConfig.class).name("name").devault(DEFAULT_NAME).op(StringOp.DEFAULT).build(),
            Property.builder().config(MeshConfig.class).name("hashing").devault(DEFAULT_HASHING)
                    .op(new EnumOp<>(HashingKind.class)).build(),
            Property.builder().config(MeshConfig.class).name("nodes").devault(NodesConfig.builder().build())
                    .op(NodesConfig.OP).build(),
            Property.builder().config(MeshConfig.class).name("local").devault(LocalConfig.builder().build())
                    .op(LocalConfig.OP).build());

    public MeshConfig() {
    }

    protected MeshConfig(String name, HashingKind hashing, NodesConfig nodes, LocalConfig local) {
        this.name = name;
        this.hashing = hashing;
        this.nodes = nodes;
        this.local = local;
    }

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

    @SuppressWarnings("unchecked")
    public static MeshConfig fromYaml(String yamlText) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlText);
        return fromMap(map);
    }

    @SuppressWarnings("unchecked")
    public static MeshConfig fromYaml(InputStream yamlStream) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlStream);
        return fromMap(map);
    }

    @SuppressWarnings("unchecked")
    public static MeshConfig fromYaml(Reader yamlReader) {
        var yaml = new Yaml();
        var map = (Map<String, Object>) yaml.load(yamlReader);
        return fromMap(map);
    }

    public static MeshConfig fromMap(Map<String, Object> map) {
        var r = new MeshConfig();
        r.withMap("", map);
        return r;
    }

}
