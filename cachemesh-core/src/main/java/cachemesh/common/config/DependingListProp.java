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
import java.util.List;
import java.util.Map;

import cachemesh.common.config.op.BeanOp;
import cachemesh.common.config.op.ListOp;

public class DependingListProp<T extends Bean, K> extends ReflectProp<List<T>> {

    public DependingListProp(Class<?> configClass, Class<T> elementType, String name, Prop<K> dependedProp,
            Map<K, ? extends BeanOp<? extends T>> opMap) {
        super(configClass, name, null, new ListOp<T>(null) {

            @Override
            public List<T> populate(String hint, Map<String, Object> parent, Object value) {
                var elementOp = elementOp(hint, value);

                var r = new ArrayList<T>();
                int i = 0;

                for (var childObj : (Iterable<?>) value) {
                    var childHint = String.format("%s[%d]", hint, i);
                    var child = elementOp.populate(childHint, parent, childObj);
                    r.add(child);
                    i++;
                }

                return r;
            }

            @Override
            @SuppressWarnings("unchecked")
            public TypeOp<? extends T> elementOp(String hint, Object value) {
                var map = (Map<String, Object>) value;
                var key = map.get(dependedProp.name());
                return opMap.get(key);
            }
        });
    }

}
