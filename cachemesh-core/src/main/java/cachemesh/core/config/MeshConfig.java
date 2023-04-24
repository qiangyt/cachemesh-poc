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
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
public class MeshConfig {

    public static enum HashingKind {
        murmur(MurmurHash.DEFAULT);

        public final Hashing instance;

        private HashingKind(Hashing instance) {
            this.instance = instance;
        }
    }

    public static final String DEFAULT_NAME = "test";

    public static final HashingKind DEFAULT_HASHING = HashingKind.murmur;

    private String name = DEFAULT_NAME;

    private HashingKind hashing = DEFAULT_HASHING;

    private MembersConfig nodes;

    private LocalConfig local;

    private final TransportRegistry transportRegistry;

    private final LocalCacheRegistry localCacheRegistry;

    public MeshConfig(TransportRegistry transportRegistry, LocalCacheRegistry localCacheRegistry) {
        this.transportRegistry = transportRegistry;
        this.localCacheRegistry = localCacheRegistry;
    }

    @Builder
    private MeshConfig(TransportRegistry transportRegistry, LocalCacheRegistry localCacheRegistry, String name,
            HashingKind hashing, MembersConfig nodes, LocalConfig local) {
        this(transportRegistry, localCacheRegistry);

        this.name = name;
        this.hashing = hashing;
        this.nodes = nodes;
        this.local = local;
    }

}
