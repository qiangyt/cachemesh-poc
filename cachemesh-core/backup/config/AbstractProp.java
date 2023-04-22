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

public abstract class AbstractProp<T> implements Prop<T> {

    private final String name;

    private final T devault;

    private final TypeOp<? extends T> op;

    protected AbstractProp(String name, T devault, TypeOp<? extends T> op) {
        this.name = name;
        this.devault = devault;
        this.op = op;
    }

	@Override
    public T devault() {
        return this.devault;
    }

	@Override
    public String name() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Prop<T> that;
        try {
            that = (Prop<T>) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return that.name().equals(name());
    }

	@Override
    public TypeOp<? extends T> op(Map<String, Object> object) {
        return this.op;
    }

	@Override
    public void set(String hint, Map<String, Object> object) {
        var op = op(object);
        Object value = object.get(name());
        doSet(op, hint, object, value);
    }

    public abstract void doSet(TypeOp<? extends T> op, String hint, Map<String, Object> object, Object value);

}
