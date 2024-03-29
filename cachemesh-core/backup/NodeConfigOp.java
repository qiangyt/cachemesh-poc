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

import java.util.Map;

import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.DynamicOp;
import cachemesh.common.config.op.SimpleUrlOp;
import cachemesh.common.misc.SimpleURL;

public class NodeConfigOp extends DynamicOp<NodeConfig> {

    public NodeConfigOp(Map<Object, ? extends BeanOp<? extends NodeConfig>> opMap) {
        super(NodeConfig.class, opMap);
    }

    @Override
    public Object extractKey(String hint, Map<String, Object> parent, Map<String, Object> value) {
        if (value.containsKey("url") == false) {
            throw new IllegalArgumentException(hint + ": url is required");
        }

        SimpleURL url;

        var urlObj = value.get("url");
        if (urlObj instanceof SimpleURL) {
            // got cached url
            url = (SimpleURL) urlObj;
        } else {
            url = SimpleUrlOp.DEFAULT.build(hint, parent, urlObj);
            value.put("url", url); // cache it to prevent conversion again and again
        }

        return url.getProtocol();
    }

}
