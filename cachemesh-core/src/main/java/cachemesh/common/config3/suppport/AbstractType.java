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
package cachemesh.common.config3.suppport;

import java.util.ArrayList;

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.IncompatibleTypeException;
import cachemesh.common.config3.Type;

public abstract class AbstractType<T> implements Type<T> {

    public Iterable<Class<?>> convertableClasses() {
        return null;
    }

    @Override
    public Type<?> getElementType() {
        return null;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean isMap() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convert(ConvertContext ctx, Object value) {
        if (value == null) {
            return null;
        }

        if (getKlass().isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (isConvertable(value) == false) {
            throw invalidValueClassError(ctx, value.getClass());
        }

        return doConvert(ctx, value);
    }

    public boolean isConvertable(Object value) {
        var classes = convertableClasses();
        if (classes == null) {
            return false;
        }

        var actual = value.getClass();
        for (var expected : classes) {
            if (expected.isAssignableFrom(actual)) {
                return true;
            }
        }

        return false;
    }

    protected abstract T doConvert(ConvertContext ctx, Object value);

    public IllegalArgumentException invalidValueClassError(ConvertContext ctx, Class<?> actual) {
        var classes = new ArrayList<Class<?>>();
        classes.add(getKlass());

        var others = convertableClasses();
        if (others != null) {
            for (var other : others) {
                classes.add(other);
            }
        }

        return new IncompatibleTypeException(actual, classes);
    }

}
