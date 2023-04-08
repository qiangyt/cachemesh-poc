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

import java.util.Map;

public class DependingProperty<T extends SomeConfig, K> extends Property<T> {

    public DependingProperty(Class<?> configClass, Class<T> propertyClass, String propertyName,
            Property<K> dependedProperty, Map<K, ? extends NestedOp<? extends T>> dispatchOpMap) {
        super(configClass, propertyName, null, new NestedOp<T>(propertyClass) {
            @Override
            public T newValue(String hint, Map<String, Object> parentObject, Map<String, Object> map) {
                var dependedValue = parentObject.get(dependedProperty.name());
                var targetOp = dispatchOpMap.get(dependedValue);
                return targetOp.newValue(hint, parentObject, map);
            }
        });
    }

}
