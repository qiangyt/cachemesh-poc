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
import java.util.Map;

import cachemesh.common.misc.StringHelper;

public interface Op<T> {

    Class<?> type();

    default Iterable<Class<?>> convertableTypes() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default T supply(String hint, Map<String, Object> parent, Object value) {
        return (T) value;
    }

    default T build(String hint, Map<String, Object> parent, Object value) {
        if (value == null) {
            return null;
        }

        if (type().isAssignableFrom(value.getClass())) {
            return supply(hint, parent, value);
        }

        if (isConvertable(value) == false) {
            throw invalidValueClassError(hint, value);
        }

        return convert(hint, parent, value);
    }

    default boolean isConvertable(Object value) {
        var classes = convertableTypes();
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

    default T convert(String hint, Map<String, Object> parent, Object value) {
        throw new UnsupportedOperationException("To be implemented");
    }

    default IllegalArgumentException invalidValueClassError(String hint, Object value) {
        var classes = new ArrayList<Class<?>>();
        classes.add(type());

        var others = convertableTypes();
        if (others != null) {
            for (var other : others) {
                classes.add(other);
            }
        }

        var msg = String.format("%s: expect be %s, but got %s", hint, StringHelper.join("/", classes),
                value.getClass());
        return new IllegalArgumentException(msg);
    }

}
