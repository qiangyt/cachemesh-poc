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

import cachemesh.common.config.Prop;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.op.IntegerOp;
import cachemesh.common.config.op.ReflectBeanOp;
import cachemesh.common.config.op.StringOp;
import cachemesh.common.misc.SimpleURL;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrpcNodeConfig extends NodeConfig {

    public static final ReflectBeanOp<GrpcNodeConfig> OP = new ReflectBeanOp<>(GrpcNodeConfig.class);

    public static final String PROTOCOL = "grpc";

    public static final int DEFAULT_PORT = 12001;

    public static final Prop<String> HOST_PROP = Prop.<String> builder().config(GrpcNodeConfig.class).name("host")
            .op(StringOp.DEFAULT).build();

    public static final Prop<Integer> PORT_PROP = Prop.<Integer> builder().config(GrpcNodeConfig.class).name("port")
            .devault(DEFAULT_PORT).op(IntegerOp.DEFAULT).build();

    public static final Iterable<Prop<?>> PROPS = ConfigHelper.props(NodeConfig.PROPS, HOST_PROP, PORT_PROP);

    // TODO: credential

    private String target;

    private String host;

    private int port;

    @Builder
    public GrpcNodeConfig(SimpleURL url) {
        super(url);
    }

    @Builder
    public GrpcNodeConfig(SimpleURL url, boolean local, int startTimeout, int stopTimeout) {
        super(url, local, startTimeout, stopTimeout);
    }

    @Builder
    public GrpcNodeConfig(String host, int port, boolean local, int startTimeout, int stopTimeout) {
        super(local, startTimeout, stopTimeout);

        this.host = host;
        this.port = port;
    }

    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

    public String getTarget() {
        if (this.target == null) {
            this.target = String.format("%s:%d", getHost(), getPort());
        }
        return this.target;
    }

    public ManagedChannel createClientChannel() {
        return Grpc.newChannelBuilder(getTarget(), InsecureChannelCredentials.create()).build();
    }

    @Override
    public void setUrl(SimpleURL url) {
        super.setUrl(url);

        setHost(url.getHost());
        setPort(url.getPort());
    }

}
