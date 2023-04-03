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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;

public interface SomeConfig extends Mappable {

    Collection<Property<?>> properties();

    static Collection<Property<?>> buildProperties(Property<?>... array) {
        var list = Arrays.asList(array);
        return Collections.unmodifiableList(list);
    }

    static Collection<Property<?>> buildProperties(Collection<Property<?>> supers, Property<?>... array) {
        var list = new ArrayList<Property<?>>(supers.size() + array.length);
        list.addAll(supers);
        list.addAll(Arrays.asList(array));
        return Collections.unmodifiableList(list);
    }

    @Override
    default Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();
        for (var p : properties()) {
            r.put(p.propertyName(), p.get(this));
        }
        return r;
    }

    default void withMap(String path, Map<String, Object> m) {
        for (var p : properties()) {
            var name = p.propertyName();
            if (m.containsKey(name)) {
                Object value = m.get(name);
                p.set(path, this, value);
            }
        }
    }

    /*
     * @SuppressWarnings("unchecked") default void withAny(String path, Object object) { if (object instanceof Map) {
     * withMap(path, (Map<String, Object>)object); return; } throw new IllegalArgumentException(getClass() +
     * " expects to read a map only"); }
     */

}
