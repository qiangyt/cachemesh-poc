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
package cachemesh.core.cache.bean;

import javax.annotation.Nonnull;

import cachemesh.core.cache.bean.Value.Status;
import lombok.Getter;

@Getter
public class GetResult<T> {

    private static final GetResult<?> NOT_FOUND = new GetResult<>(Status.NOT_FOUND, null, 0);

    private static final GetResult<?> NO_CHANGE = new GetResult<>(Status.NO_CHANGE, null, 0);

    @Nonnull
    private final Status status;

    private final T value;

    private final long version;

    public GetResult(@Nonnull Status status, T value, long version) {
        this.status = status;
        this.value = value;
        this.version = version;
    }

    public static <T> GetResult<T> ok(T value, long version) {
        return new GetResult<>(Status.OK, value, version);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> GetResult<T> notFound() {
        return (GetResult<T>) NOT_FOUND;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> GetResult<T> noChange() {
        return (GetResult<T>) NO_CHANGE;
    }

}
