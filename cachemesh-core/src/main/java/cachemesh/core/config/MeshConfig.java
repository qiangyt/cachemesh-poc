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
import cachemesh.common.config.EnumAccessor;
import cachemesh.common.config.NestedAccessor;
import cachemesh.common.config.Accessor;
import cachemesh.common.config.SomeConfig;
import cachemesh.common.config.StringAccessor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class MeshConfig implements SomeConfig, HasName {

    public static enum HashingKind {
        murmur(MurmurHash.DEFAULT);

        public final Hashing instance;

        private HashingKind(Hashing instance) {
            this.instance = instance;
        }
    }

    private String name;

    private HashingKind hashing;

    private NodesConfig nodes;

    private LocalConfig local;

    public static final Collection<Accessor<?>> ACCESSORS = SomeConfig.buildAccessors(
            new StringAccessor(MeshConfig.class, "name", null),
            new EnumAccessor<HashingKind>(MeshConfig.class, HashingKind.class, "hashing", HashingKind.murmur),
            new NestedAccessor<NodesConfig>(MeshConfig.class, NodesConfig.class, "nodes", null),
            new NestedAccessor<LocalConfig>(MeshConfig.class, LocalConfig.class, "local", null));

    @Override
    public Collection<Accessor<?>> accessors() {
        return ACCESSORS;
    }

}
