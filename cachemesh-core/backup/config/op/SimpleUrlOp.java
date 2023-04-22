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
package cachemesh.common.config.op;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.misc.SimpleURL;

public class SimpleUrlOp extends AbstractOp<SimpleURL> {

    public static final SimpleUrlOp DEFAULT = new SimpleUrlOp();

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(String.class, URL.class);

    @Override
    public Class<?> klass() {
        return SimpleURL.class;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    public SimpleURL convert(String hint, Map<String, Object> parent, Object value) {
        var clazz = value.getClass();

        if (clazz == String.class) {
            try {
                return new SimpleURL((String) value);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }

        return new SimpleURL((URL) value);
    }

}
