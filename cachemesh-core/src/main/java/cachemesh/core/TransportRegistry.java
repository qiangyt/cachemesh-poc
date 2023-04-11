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

import java.util.Map;

import cachemesh.common.config.Prop;
import cachemesh.common.config.ReflectProp;
import cachemesh.common.config.op.BeanOp;
import cachemesh.common.misc.SimpleRegistry;
import cachemesh.core.config.NodeConfig;
import cachemesh.core.spi.TransportProvider;
import cachemesh.grpc.GrpcConfig;
import cachemesh.lettuce.LettuceConfig;

public class TransportRegistry extends SimpleRegistry<String, TransportProvider> {

    public static final Map<Object, ? extends BeanOp<? extends NodeConfig>> FACTORY = Map.of(LettuceConfig.PROTOCOL,
            LettuceConfig.OP, GrpcConfig.PROTOCOL, GrpcConfig.OP);

    public static final TransportRegistry DEFAULT = new TransportRegistry();

    private final Iterable<Prop<?>> configProps;

    public TransportRegistry() {
        this.configProps = buildConfigProps();
    }

    public Iterable<Prop<?>> buildConfigProps() {

    }

    @Override
    protected String supplyKey(String protocol) {
        return protocol;
    }

    public Iterable<Prop<?>> configProps() {
        return this.configProps;
    }

}
