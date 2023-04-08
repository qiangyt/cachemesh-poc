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

import java.util.List;
import java.util.Map;

public class DependingListProperty<T extends SomeConfig, K> extends Property<List<T>> {

    public DependingListProperty(Class<?> configClass, Class<T> propertyElementClass, String propertyName,
            Property<K> dependedProperty, Map<K, ? extends NestedOp<? extends T>> dispatchOpMap) {
        super(configClass, propertyName, null, new ListOp<T>(null) {

            @Override
            @SuppressWarnings("unchecked")
            public Operator<? extends T> getElementOp(String hint, Object parentObject, Object value) {
                var map = (Map<String, Object>) parentObject;
                var key = map.get(dependedProperty.name());
                return dispatchOpMap.get(key);
            }
        });
    }

}
