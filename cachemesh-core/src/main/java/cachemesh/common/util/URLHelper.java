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
package cachemesh.common.util;

import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

public class URLHelper {

    // https://github.com/anthonynsimon/jurl/tree/master/src/main/java/com/anthonynsimon/url
    // this is not a standard-compliant URL query parser.
    public static Map<String, String> parseQuery(String query) {
        var r = new HashMap<String, String>();
        String[] pairs = query.split("&");
        for (var pair : pairs) {
            int pos = pair.indexOf('=');
            if (pos > 0) {
                String key = pair.substring(0, pos);
                String value = pair.substring(pos + 1);
                r.put(key, value);
            }
        }
        return r;
    }

    public static void registerHandler(Class<? extends URLStreamHandler> handlerClass) {
        final String KEY = "java.protocol.handler.pkgs";

        var enclosingPkg = handlerClass.getPackageName();
        var pkg = enclosingPkg.substring(0, enclosingPkg.lastIndexOf('.'));

        var prev = System.getProperty(KEY);
        if (prev == null) {
            System.setProperty(KEY, pkg);
        } else if (prev.contains(pkg) == false) {
            System.setProperty(KEY, prev + "|" + pkg);
        }
    }

}
