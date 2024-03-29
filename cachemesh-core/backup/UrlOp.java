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
package cachemesh.common.config;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UrlOp implements Operator<URL> {

    public static final UrlOp DEFAULT = new UrlOp();

    @Override
    public Class<?> type() {
        return URL.class;
    }

    @Override
    public Collection<Class<?>> convertableTypes() {
        return ConfigHelper.STRING;
    }

    @Override
    public URL convert(String hint, Object value) {
        try {
            return new URL((String) value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
