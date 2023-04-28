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

import java.util.Map;

import cachemesh.common.config.suppport.ChildContext;
import cachemesh.common.misc.ClassCache;
import lombok.NonNull;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

public interface ConfigContext {

    @NonNull
    ClassCache getClassCache();

    @NonNull
    TypeRegistry getTypeRegistry();

    @Nullable
    Object getValue(@NonNull Path relative);

    @Nullable
    Object setValue(@NonNull Path relative, @Nullable Object newValue);

    @NonNull
    Map<String, Object> getRootValue();

    @NonNull
    Path getPath();

    @Nullable
    ConfigContext getParent();

    @NonNull
    ConfigContext getRoot();

    @NonNull
    default ConfigContext createChild(@Nonnull String name) {
        return new ChildContext(this, name);
    }

    @NonNull
    default ConfigContext createChild(int index) {
        checkArgument(index >= 0);
        return new ChildContext(this, index);
    }

}
