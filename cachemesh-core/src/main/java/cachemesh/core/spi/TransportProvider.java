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
package cachemesh.core.spi;

import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.core.cache.LocalTransport;
import cachemesh.core.config.NodeConfig;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

public interface TransportProvider extends NodeHook {

    default boolean bindLocalTransport(@Nonnull NodeConfig nodeConfig, @Nonnull LocalTransport localTranport) {
        checkNotNull(nodeConfig);
        checkNotNull(localTranport);

        return false;
    }

    @Nonnull
    Transport createRemoteTransport(@Nonnull NodeConfig nodeConfig);

    @Nonnull
    BeanType<? extends NodeConfig> resolveConfigType(@Nonnull TypeRegistry typeRegistry);

}
