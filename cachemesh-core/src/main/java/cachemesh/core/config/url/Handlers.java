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
package cachemesh.core.config.url;

import java.net.URL;

import cachemesh.common.util.URLHelper;

public class Handlers {

    public static void registerAll() {
        URLHelper.registerHandler(cachemesh.core.config.url.grpc.Handler.class);
        URLHelper.registerHandler(cachemesh.core.config.url.lettuce.Handler.class);
    }

    public static void main(String[] args) throws Exception {
        registerAll();

        // URL url = new URL(null, "classpath://com.mycompany/hello-world.xml", new
        // Handler(ClassLoader.getSystemClassLoader()));
        URL url = new URL("grpc://com.mycompany/hello-world.xml");
        System.out.println(url);

        url = new URL("lettuce://qiangyt:pwd@com.mycompany:3379/hello-world.xml?xy=y");
        System.out.println(url);
        System.out.println("authority: " + url.getAuthority());
        System.out.println("host: " + url.getHost());
        System.out.println("protocol: " + url.getProtocol());
        System.out.println("port: " + url.getPort());
        System.out.println("defaultPort: " + url.getDefaultPort());
        System.out.println("path: " + url.getPath());
        System.out.println("query: " + url.getQuery());
        System.out.println("ref: " + url.getRef());
        System.out.println("userInfo: " + url.getUserInfo());
    }
}
