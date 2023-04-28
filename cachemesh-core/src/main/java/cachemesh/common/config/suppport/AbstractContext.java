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
package cachemesh.common.config.suppport;

import java.util.Map;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public abstract class AbstractContext implements ConfigContext {

    @Nonnull
    private final ClassCache classCache;

    @Nonnull
    private final TypeRegistry typeRegistry;

    @Nonnull
    private final Map<String, Object> rootValue;

    public AbstractContext(@Nonnull ClassCache classCache, @Nonnull TypeRegistry typeRegistry,
            @Nonnull Map<String, Object> rootValue) {
        this.classCache = checkNotNull(classCache);
        this.typeRegistry = checkNotNull(typeRegistry);
        this.rootValue = checkNotNull(rootValue);
    }

    @Override
    @Nullable
    public Object getValue(@Nonnull Path relative) {
        return getRoot().getValue(relative);
    }

    @Override
    @Nullable
    public Object setValue(@Nonnull Path relative, @Nullable Object newValue) {
        return getRoot().setValue(relative, newValue);
    }

}
