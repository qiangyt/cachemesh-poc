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

import cachemesh.common.config.NestedOp;
import cachemesh.common.config.StringOp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import cachemesh.common.config.NestedDynamicOp;

public class NodeConfigOp extends NestedDynamicOp<NodeConfig> {

    public static final Map<Object, NestedOp<NodeConfig>> FACTORY = Map.of(LettuceConfig.PROTOCOL, LettuceConfig.OP,
            GrpcConfig.PROTOCOL, GrpcConfig.OP);

    public static final NodeConfigOp DEFAULT = new NodeConfigOp();

    public NodeConfigOp() {
        super(NodeConfig.class, FACTORY);
    }

    @Override
    public Object extractKey(String hint, Map<String, Object> map) {
        if (map.containsKey("url") == false) {
            throw new IllegalArgumentException(hint + ": url is required");
        }

        var urlText = StringOp.DEFAULT.convert(hint, map.get("url"));
        URL url;
        try {
            url = new URL(urlText);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url.getProtocol();
    }

}
