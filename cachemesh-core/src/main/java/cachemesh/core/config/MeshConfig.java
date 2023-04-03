/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Collection;

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
            Property.<String> builder().configClass(MeshConfig.class).propertyName("name").defaultValue(DEFAULT_NAME)
                    .op(StringOp.DEFAULT).build(),
            Property.<HashingKind> builder().configClass(MeshConfig.class).propertyName("hashing")
                    .defaultValue(DEFAULT_HASHING).op(new EnumOp<>(HashingKind.class)).build(),
            Property.<NodesConfig> builder().configClass(MeshConfig.class).propertyName("nodes")
                    .defaultValue(NodesConfig.builder().build()).op(NodesConfig.OP).build(),
            Property.<LocalConfig> builder().configClass(MeshConfig.class).propertyName("local")
                    .defaultValue(LocalConfig.builder().build()).op(LocalConfig.OP).build());

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
    }

}
