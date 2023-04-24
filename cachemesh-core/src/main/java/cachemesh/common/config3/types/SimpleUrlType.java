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
package cachemesh.common.config3.types;

import java.net.MalformedURLException;
import java.net.URL;

import cachemesh.common.config3.suppport.AbstractType;
import cachemesh.common.config3.ConfigHelper;
import cachemesh.common.config3.ConvertContext;
import cachemesh.common.misc.SimpleURL;

public class SimpleUrlType extends AbstractType<SimpleURL> {

    public static final SimpleUrlType DEFAULT = new SimpleUrlType();

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(String.class, URL.class);

    @Override
    public Class<?> getKlass() {
        return SimpleURL.class;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return CONVERTABLES;
    }

    @Override
    protected SimpleURL doConvert(ConvertContext ctx, Object value) {
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
