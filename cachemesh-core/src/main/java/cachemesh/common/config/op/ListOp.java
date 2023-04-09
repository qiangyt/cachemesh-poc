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
package cachemesh.common.config.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Op;

public class ListOp<T> implements Op<List<T>> {

    public static final Iterable<Class<?>> CONVERTABLES = ConfigHelper.convertables(Iterable.class);

    private final Op<? extends T> elementOp;

    public ListOp(Op<? extends T> elementOp) {
        this.elementOp = elementOp;
    }

    public Op<? extends T> elementOp(String hint, Object value) {
        return this.elementOp;
    }

    @Override
    public Class<?> type() {
        return List.class;
    }

    @Override
    public Iterable<Class<?>> convertableTypes() {
        return CONVERTABLES;
    }

    @Override
    public List<T> supply(String hint, Map<String, Object> parent, Object value) {
        return build(hint, parent, value);
    }

    @Override
    public List<T> convert(String hint, Map<String, Object> parent, Object value) {
        return build(hint, parent, value);
    }

    public List<T> build(String hint, Map<String, Object> parent, Object value) {
        var elementOp = elementOp(hint, value);

        var r = new ArrayList<T>();
        int i = 0;

        for (var childObj : (Iterable<?>) value) {
            var childHint = String.format("%s[%d]", hint, i);
            var child = elementOp.build(childHint, parent, childObj);
            r.add(child);
            i++;
        }

        return r;
    }

}
