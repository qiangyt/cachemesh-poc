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
package cachemesh.core;

import cachemesh.common.Registry;
import cachemesh.core.spi.TransportProvider;
import cachemesh.grpc.GrpcConfig;
import cachemesh.grpc.GrpcTransportProvider;
import cachemesh.redis.lettuce.LettuceConfig;
import cachemesh.redis.lettuce.LettuceTransportProvider;

public class TransportRegistry extends Registry<String, TransportProvider> {

    public static final TransportRegistry DEFAULT = new TransportRegistry();

    static {
        TransportRegistry.DEFAULT.register(GrpcConfig.PROTOCOL, GrpcTransportProvider.DEFAULT);
        TransportRegistry.DEFAULT.register(LettuceConfig.PROTOCOL, LettuceTransportProvider.DEFAULT);
    }

    @Override
    protected String retrieveKey(String protocol) {
        return protocol;
    }

}
