/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cachemesh.common.util;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.Lists;

public class StringHelper {

    public static String capitalize(String t) {
        if (t == null || t.isBlank()) {
            return t;
        }
        return t.substring(0, 1).toUpperCase() + t.substring(1);
    }

    public static <T> String toString(T[] array) {
        if (array == null) {
            return null;
        }
        return Lists.newArrayList(array).toString();
    }

    public static <T> String join(String separator, Collection<T> texts) {
        return join(separator, texts.toArray(new String[texts.size()]));
    }

    public static <T> String join(String separator, T[] array) {
        var r = new StringBuilder(array.length * 64);
        var isFirst = true;
        for (var obj : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                r.append(separator);
            }
            r.append(Objects.toString(obj));
        }
        return r.toString();
    }

    public static boolean isBlank(String str) {
        return (str == null || str.length() == 0 || str.trim().length() == 0);
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

}
