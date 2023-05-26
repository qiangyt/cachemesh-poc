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
package cachemesh.common.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

public class JacksonHelper {

    public static final Jackson JACKSON = Jackson.DEFAULT;

    @Nullable
    public static String pretty(@Nullable Object object) {
        return JACKSON.pretty(object);
    }

    @Nullable
    public static String to(@Nullable Object object) {
        return JACKSON.toString(object, false);
    }

    @Nullable
    public static <T> T from(@Nullable String json, @Nonnull Class<T> clazz) {
        requireNonNull(clazz);
        return JACKSON.from(json, clazz);
    }

    @Nullable
    public static <T> T from(@Nullable String json, @Nonnull TypeReference<T> typeReference) {
        requireNonNull(typeReference);
        return JACKSON.from(json, typeReference);
    }

}
