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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependingListProperty<T, K> extends DependingProperty<List<T>, K> {

    public DependingListProperty(Class<?> configClass, String propertyName, Property<K> dependedProperty,
		Map<K, Operator<? extends T>> dispatchOpMap) {
        super(configClass, propertyName, dependedProperty, buildListDispatchOpMap(dispatchOpMap));
    }

	public static <T, K>
		Map<K, ListOp<T>> buildListDispatchOpMap(Map<K, Operator<? extends T>> dispatchOpMap) {

		var r = new HashMap<K, ListOp<T>>();
		for (var entry: dispatchOpMap.entrySet()) {
			var kind = entry.getKey();
			var dispatchOp = entry.getValue();
			r.put(kind, new ListOp<T>(dispatchOp));
		}

		return r;
	}

}
