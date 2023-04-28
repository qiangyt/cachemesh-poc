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

import lombok.Getter;

@Getter
public class RemoteValue {

    public static enum Status {
        NULL, OK, NO_CHANGE,
    }

    @Nonnull
    private final Status status;

    private final byte[] bytes;

    private final long version;

    public RemoteValue(@Nonnull Status status, byte[] bytes, long version) {
        this.status = status;
        this.bytes = bytes;
        this.version = version;
    }

}
