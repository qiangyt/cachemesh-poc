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

import cachemesh.common.misc.Serderializer;
import javax.annotation.Nonnull;

public interface Value {

    public static enum Status {

        NOT_FOUND, OK, NO_CHANGE, REDIRECT,

    }

    boolean hasValue();

    boolean isNullValue();

    <T> T getObject(@Nonnull Serderializer serder, @Nonnull Class<?> valueClass);

    <T> T getObject();

    byte[] getBytes(@Nonnull Serderializer serder);

    byte[] getBytes();

    long getVersion();

}
