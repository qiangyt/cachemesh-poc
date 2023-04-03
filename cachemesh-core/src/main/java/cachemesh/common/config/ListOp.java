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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class ListOp<T> extends Operator<List<T>> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Iterable.class));

    private final Operator<? extends T> elementOp;

    public ListOp(Operator<? extends T> elementOp) {
        this.elementOp = elementOp;
    }

    @Override
    public List<T> copy(String hint, List<T> value) {
        return new ArrayList<>(value);
    }

    @Override
    public Class<?> propertyClass() {
        return List.class;
    }

    @Override
    public List<T> createZeroValue() {
        return new ArrayList<T>();
    }

    @Override
    public Collection<Class<?>> convertableClasses() {
        return CONVERTABLE_CLASSES;
    }

    @Override
    public List<T> supply(String hint, Object value) {
        return doConvert(hint, value);
    }

    @Override
    public List<T> doConvert(String hint, Object value) {
        var r = new ArrayList<T>();

        var eltOp = getElementOp();
        int i = 0;

        for (var childObj : (Iterable<?>) value) {
            var childHint = String.format("%s[%d]", hint, i);
            var child = eltOp.convert(childHint, childObj);
            r.add(child);
            i++;
        }

        return r;
    }

}
