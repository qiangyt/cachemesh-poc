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

public class DependingProp<T extends Bean, K> extends Prop<T> {

    public DependingProp(Prop<T> target, Prop<K> depended, Map<K, ? extends TypeOp<? extends T>> opMap) {
        super(target.name(), null, new DelegateOp<T>() {

			@Override
			public TypeOp<T> target() {
				return target.getClass();
			}

        });
    }

	@Override
	public T get(Object object) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'get'");
	}

	@Override
	public void doSet(TypeOp<? extends T> op, String hint, Map<String, Object> object, Object value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'doSet'");
	}

}
