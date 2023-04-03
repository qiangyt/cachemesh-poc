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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;
import lombok.Getter;

@Getter
public class TransportURL implements Mappable {

    public static final String URL_SEPARATOR = "://";

    private final String protocol;

    private final String target;

    public TransportURL(String protocol, String target) {
        this.protocol = protocol;
        this.target = target;
    }

    public String formatUrl() {
        return formatUrl(getProtocol(), getTarget());
    }

    public void ensureProtocol(String expectedProtocol) {
        if (getProtocol().equals(expectedProtocol) == false) {
            throw new IllegalArgumentException("protocol must be " + expectedProtocol);
        }
    }

    public static String formatUrl(String protocol, String target) {
        return protocol + URL_SEPARATOR + target;
    }

    public static TransportURL parseUrl(String url) {
        int sep = url.indexOf(URL_SEPARATOR);
        if (sep <= 0) {
            throw new IllegalArgumentException("url should follow the format: <url>" + URL_SEPARATOR + "<target>");
        }

        String protocol = url.substring(0, sep);
        String target = url.substring(sep + URL_SEPARATOR.length());

        return new TransportURL(protocol, target);
    }

    @Override
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();

        r.put("protocol", getProtocol());
        r.put("target", getTarget());

        return r;
    }

    public static String getTarget(Map<String, Object> configMap) {
        return "target";
    }

}
