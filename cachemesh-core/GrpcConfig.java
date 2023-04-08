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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.util.StringHelper;
import cachemesh.core.TransportURL;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.Getter;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GrpcConfig extends NodeConfig {

    public static final String PROTOCOL = "grpc";

    public static final String DEFAULT_HOST = "localhost";

    @Builder.Default
    private String host = DEFAULT_HOST;

    private int port;

    private String url;

    private String target;

    private boolean remote;

    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    public ManagedChannel createClientChannel() {
        return Grpc.newChannelBuilder(getTarget(), InsecureChannelCredentials.create()).build();
    }

    @Override
    public Map<String, Object> toMap() {
        var configMap = super.toMap();

        configMap.put("url", getUrl());
        configMap.put("host", getHost());
        configMap.put("port", getPort());

        return configMap;
    }

    public static GrpcConfig from(String url) {
        var transport = TransportURL.parseUrl(url);
        transport.ensureProtocol(PROTOCOL);

        var configMap = parseTarget(transport.getTarget());
        return from(configMap);
    }

    public static GrpcConfig from(Map<String, Object> configMap) {
        var url = (String) configMap.get("url");
        var host = (String) configMap.get("host");
        var port = (Integer) configMap.get("port");

        String target;

        if (StringHelper.isBlank(url)) {
            target = formatTarget(host, port);
            url = TransportURL.formatUrl(PROTOCOL, target);
        } else {
            if (StringHelper.isBlank(host) == false || port != null) {
                throw new IllegalArgumentException("host+port is exclusive with url");
            }

            var transport = TransportURL.parseUrl(url);
            transport.ensureProtocol(PROTOCOL);

            target = transport.getTarget();
            configMap = parseTarget(target);

            host = (String) configMap.get("host");
            port = (Integer) configMap.get("port");
        }

        var startTimeoutSeconds = (Integer) configMap.get("startTimeoutSeconds");
        var stopTimeoutSeconds = (Integer) configMap.get("stopTimeoutSeconds");
        var remote = (Boolean) configMap.get("remote");

        return builder().host(host).port(port.intValue()).url(url).target(target)
                .startTimeoutSeconds(startTimeoutSeconds).stopTimeoutSeconds(stopTimeoutSeconds).remote(remote).build();
    }

    public static String formatTarget(String host, int port) {
        return host + ":" + port;
    }

    public static Map<String, Object> parseTarget(String name) {
        int sep = name.indexOf(":");
        if (sep <= 0) {
            throw new IllegalArgumentException("name should follow the format: <host>:<port>");
        }

        String host = name.substring(0, sep);
        String portS = name.substring(sep + ":".length());
        int port = Integer.parseInt(portS);

        var r = new HashMap<String, Object>();
        r.put("host", host);
        ;
        r.put("port", port);
        return r;
    }

}
