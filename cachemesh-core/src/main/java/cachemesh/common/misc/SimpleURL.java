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
package cachemesh.common.misc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import lombok.Getter;

@Getter
public class SimpleURL {

    private final URL url;

    private final Map<String, String> query;

    public SimpleURL(URL url) {
        this.url = url;

        var query = URLHelper.parseQuery(url.getQuery());
        this.query = Collections.unmodifiableMap(query);
    }

    public SimpleURL(String urlText) throws MalformedURLException {
        this(new URL(urlText));
    }

    public String getPath() {
        return this.url.getPath();
    }

    public String getAuthority() {
        return this.url.getAuthority();
    }

    public int getPort() {
        return this.url.getPort();
    }

    public String getProtocol() {
        return this.url.getProtocol();
    }

    public String getHost() {
        return this.url.getHost();
    }

    public String getRef() {
        return this.url.getRef();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof SimpleURL) {
            var that = (SimpleURL) obj;
            return this.url.equals(that.url);
        }
        if (obj instanceof URL) {
            return this.url.equals((URL) obj);
        }
        return false;
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public String toString() {
        return this.url.toString();
    }

}
