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
package cachemesh.common.misc;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

public class StringHelper {

    @Nullable
    public static String capitalize(@Nullable String t) {
        if (t == null || t.isBlank()) {
            return t;
        }
        return t.substring(0, 1).toUpperCase() + t.substring(1);
    }

    @Nullable
    public static <T> String toString(@Nullable T[] array) {
        if (array == null) {
            return null;
        }
        return Lists.newArrayList(array).toString();
    }

    /*
     * @Nonnull public static <T> String join(@Nonnull String separator, @Nonnull Collection<T> texts) { return
     * join(separator, texts.toArray()); }
     *
     * @Nonnull public static <T> String join(@Nonnull String separator, @Nonnull T[] array) { var r = new
     * StringBuilder(array.length * 64); var isFirst = true; for (var obj : array) { if (isFirst) { isFirst = false; }
     * else { r.append(separator); } r.append(Objects.toString(obj)); } return r.toString(); }
     */

    public static boolean isBlank(@Nullable String str) {
        return (str == null || str.length() == 0 || str.trim().length() == 0);
    }

    public static boolean notBlank(@Nullable String str) {
        return !isBlank(str);
    }

}
