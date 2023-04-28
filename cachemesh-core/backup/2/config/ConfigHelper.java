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
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

public class ConfigHelper {

    public static Iterable<Prop<?>> props(Prop<?>... array) {
        var list = Arrays.asList(array);
        return ImmutableList.copyOf(array);
    }

    public static Iterable<Prop<?>> props(Iterable<Prop<?>> supers, Prop<?>... array) {
        var list = new ArrayList<Prop<?>>(2 * array.length);

        for (var p : supers) {
            list.add(p);
        }

        list.addAll(Arrays.asList(array));
        return ImmutableList.copyOf(list);
    }

    public static final Iterable<Class<?>> STRING = convertables(String.class);

    public static final Iterable<Class<?>> MAP = convertables(Map.class);

    public static Iterable<Class<?>> convertables(Class<?>... types) {
        var r = new ArrayList<Class<?>>(types.length);
        for (var type : types) {
            r.add(type);
        }
        return ImmutableCollection.copyOf(r);
    }

}
