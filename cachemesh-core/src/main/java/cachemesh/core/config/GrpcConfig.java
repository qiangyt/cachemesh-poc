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

import java.util.Collection;

import cachemesh.common.config.IntegerOp;
import cachemesh.common.config.Property;
import cachemesh.common.config.SomeConfig;
import cachemesh.common.config.StringOp;
import cachemesh.common.util.SimpleURL;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrpcConfig extends NodeConfig {

    public static final String PROTOCOL = "grpc";

    public static final int DEFAULT_PORT = 12001;

    // TODO: credential

    private String target;

    private String host;

    private int port;

    public static final Collection<Property<?>> PROPERTIES = SomeConfig.buildProperties(NodeConfig.PROPERTIES,
            Property.builder().config(GrpcConfig.class).name("host").op(StringOp.DEFAULT).build(), Property.builder()
                    .config(GrpcConfig.class).name("port").devault(DEFAULT_PORT).op(IntegerOp.DEFAULT).build());

    @Builder
    public GrpcConfig(SimpleURL url) {
        super(url);
    }

    @Builder
    public GrpcConfig(SimpleURL url, boolean local, int startTimeout, int stopTimeout) {
        super(url, local, startTimeout, stopTimeout);
    }

    @Builder
    public GrpcConfig(String host, int port, boolean local, int startTimeout, int stopTimeout) {
        super(local, startTimeout, stopTimeout);

        this.host = host;
        this.port = port;
    }

    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Override
    public Collection<Property<?>> properties() {
        return PROPERTIES;
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
