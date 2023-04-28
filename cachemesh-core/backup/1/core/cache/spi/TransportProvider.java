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
package cachemesh.core.cache.spi;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.core.cache.local.LocalTransport;
import cachemesh.core.cache.node.NodeHook;
import cachemesh.core.cache.transport.Transport;
import cachemesh.core.config.NodeConfig;

import javax.annotation.Nonnull;

public interface TransportProvider<TRANSPORT extends Transport, CONFIG extends NodeConfig> extends NodeHook {

    boolean bindLocalTransport(@Nonnull CONFIG nodeConfig, @Nonnull LocalTransport localTranport);

    @Nonnull
    TRANSPORT createRemoteTransport(@Nonnull CONFIG nodeConfig);

    @Nonnull
    BeanType<CONFIG> resolveConfigType(@Nonnull TypeRegistry typeRegistry);

}
