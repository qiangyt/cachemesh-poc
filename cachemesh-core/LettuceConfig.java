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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class LettuceConfig extends NodeConfig {

    public static final String PROTOCOL = "redis";

    private final String host;

    private final int port;

    private final int database;

    private final String url;

    private final String target;

    public static final String DEFAULT_SEPARATOR = "%";

    private final String keySeparator;

    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    public static String formatTarget(String host, int port, int database) {
        if (database > 0) {
            return String.format("%s:%d/%d", host, port, database);
        }
        return String.format("%s:%d", host, port);
    }

    public static Map<String, Object> parseTarget(String target) {
        int sep1 = target.indexOf(":");
        if (sep1 <= 0) {
            throw new IllegalArgumentException("name should follow the format: <host>:<port>/<database>");
        }

        String host = target.substring(0, sep1);
        String portAndDatabase = target.substring(sep1 + ":".length());

        int port;
        int database;
        int sep2 = portAndDatabase.indexOf("/");
        if (sep2 <= 0) {
            port = Integer.parseInt(portAndDatabase);
            database = 0;
        } else {
            String portS = portAndDatabase.substring(0, sep2);
            port = Integer.parseInt(portS);

            String databaseS = portAndDatabase.substring(sep2 + "/".length());
            database = Integer.parseInt(databaseS);
        }

        var r = new HashMap<String, Object>();
        r.put("host", host);
        r.put("port", port);
        r.put("database", database);
        return r;
    }

}
