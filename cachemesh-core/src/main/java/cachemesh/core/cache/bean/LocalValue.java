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

import lombok.Getter;


@Getter
public class LocalValue<T> {

    public static final Object NULL = new Object();

    private final T data;

    private final long version;

    @SuppressWarnings("unchecked")
    public static <T> LocalValue<T> Null(long version) {
        return new LocalValue<>((T)NULL, version);
    }

    public LocalValue(T data, long version) {
        this.data = data;
        this.version = version;
    }

    public boolean isNull() {
        return NULL == this;
    }

}
