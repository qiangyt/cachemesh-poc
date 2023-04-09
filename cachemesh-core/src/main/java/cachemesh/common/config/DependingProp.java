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

import cachemesh.common.config.op.BeanOp;

public class DependingProp<T extends Bean, K> extends Prop<T> {

    public DependingProp(Class<?> configClass, Class<T> type, String name, Prop<K> dependedProp,
            Map<K, ? extends BeanOp<? extends T>> opMap) {
        super(configClass, name, null, new BeanOp<T>(type) {
            @Override
            public T newValue(String hint, Map<String, Object> parent, Map<String, Object> value) {
                var dependedValue = parent.get(dependedProp.name());
                var targetOp = opMap.get(dependedValue);
                return targetOp.newValue(hint, parent, value);
            }
        });
    }

}
