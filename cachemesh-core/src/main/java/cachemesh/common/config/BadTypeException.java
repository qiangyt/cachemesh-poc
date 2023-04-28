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

import java.util.Collection;

import cachemesh.common.err.GeneralException;
import lombok.Getter;
import javax.annotation.Nonnull;

import com.google.common.base.Joiner;

@Getter
public class BadTypeException extends GeneralException {

    @Nonnull
    private Class<?> actual;

    @Nonnull
    private Collection<Class<?>> expected;

    public BadTypeException(@Nonnull Class<?> actual, @Nonnull Collection<Class<?>> expected) {
        super("expects " + Joiner.on(".").join(expected) + " but got " + actual);

        this.actual = actual;
        this.expected = expected;
    }

}
