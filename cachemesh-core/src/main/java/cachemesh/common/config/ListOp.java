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

public class ListOp<T> implements Operator<List<T>> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(Iterable.class));

    private final Operator<? extends T> elementOp;

    public ListOp(Operator<? extends T> elementOp) {
        this.elementOp = elementOp;
    }

    public Operator<? extends T> getElementOp(String hint, Object parentObject, Object value) {
        return this.elementOp;
    }

    @Override
    public Class<?> propertyClass() {
        return List.class;
    }

    @Override
    public Collection<Class<?>> convertableClasses() {
        return CONVERTABLE_CLASSES;
    }

    @Override
    public List<T> supply(String hint, Object parentObject, Object value) {
        return doConvert(hint, parentObject, value);
    }

    @Override
    public List<T> doConvert(String hint, Object parentObject, Object value) {
        return doConvert(getElementOp(hint, parentObject, value), hint, parentObject, value);
    }

    public List<T> doConvert(Operator<? extends T> elementOp, String hint, Object parentObject, Object value) {
        var r = new ArrayList<T>();
        int i = 0;

        for (var childObj : (Iterable<?>) value) {
            var childHint = String.format("%s[%d]", hint, i);
            var child = elementOp.convert(childHint, parentObject, childObj);
            r.add(child);
            i++;
        }

        return r;
    }

}
