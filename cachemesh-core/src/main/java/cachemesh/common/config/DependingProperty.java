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

public class DependingProperty<T, K> extends Property<T> {

    private final Property<K> dependedProperty;

    private final Map<K, ? extends Operator<? extends T>> dispatchOpMap;

    public DependingProperty(Class<?> configClass, String propertyName, Property<K> dependedProperty,
            Map<K, ? extends Operator<? extends T>> dispatchOpMap) {
        super(configClass, propertyName, null, null);

        this.dependedProperty = dependedProperty;
        this.dispatchOpMap = dispatchOpMap;
    }

	public Operator<? extends T> dispatchOp(Object object) {
		var dependedValue = this.dependedProperty.get(object);
        return this.dispatchOpMap.get(dependedValue);
	}

    @Override
	public void set(String hint, Object object, Object value) {
        var dispatchOp = dispatchOp(object);
        doSet(dispatchOp, hint, object, value);
    }

}
