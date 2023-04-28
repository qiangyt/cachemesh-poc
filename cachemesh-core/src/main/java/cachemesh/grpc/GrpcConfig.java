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
package cachemesh.grpc;

import java.util.Map;

import cachemesh.common.misc.SimpleURL;
import cachemesh.core.config.NodeConfig;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
@Setter
public class GrpcConfig extends NodeConfig {

    public static final String PROTOCOL = "grpc";

    public static final int DEFAULT_PORT = 12001;

    // TODO: credential

    @Nonnull
    private String target;

    @Nonnull
    private String host;

    private int port;

    @Builder
    public GrpcConfig(@Nonnull SimpleURL url) {
        super(url);
    }

    @Builder
    public GrpcConfig(@Nonnull SimpleURL url, boolean local, int startTimeout, int stopTimeout) {
        super(url, local, startTimeout, stopTimeout);
    }

    @Builder
    public GrpcConfig(@Nonnull String host, int port, boolean local, int startTimeout, int stopTimeout) {
        super(local, startTimeout, stopTimeout);

        checkNotNull(host);

        this.host = host;
        this.port = port;
    }

    @Nonnull
    @Override
    public String getTarget() {
        if (this.target == null) {
            this.target = String.format("%s:%d", getHost(), getPort());
        }
        return this.target;
    }

    @Nonnull
    public ManagedChannel createClientChannel() {
        return Grpc.newChannelBuilder(getTarget(), InsecureChannelCredentials.create()).build();
    }

    @Override
    public void setUrl(@Nonnull SimpleURL url) {
        super.setUrl(url);

        setHost(url.getHost());
        setPort(url.getPort());
    }

    @Override
    @Nonnull
    public Map<String, Object> toMap() {
        var r = super.toMap();

        r.put("host", getHost());
        r.put("port", getPort());

        return r;
    }

}
