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
package cachemesh.common.config.types;

import java.util.Map;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.err.BadStateException;
import lombok.Getter;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public abstract class KindPathingDynamicBeanType<T> extends DynamicBeanType<T> {

    @Nonnull
    private final Path kindPath;

    @Nullable
    private final Object defaultKind;

    public KindPathingDynamicBeanType(@Nonnull TypeRegistry typeRegistry, @Nonnull Class<T> klass,
            @Nonnull Path kindPath, @Nullable Object defaultKind) {
        super(typeRegistry, klass);

        this.kindPath = checkNotNull(kindPath);
        this.defaultKind = defaultKind;
    }

    @Override
    @Nonnull
    public Object extractKind(@Nonnull ConfigContext ctx, @Nonnull Map<String, Object> propValues) {
        checkNotNull(ctx);
        checkNotNull(propValues);

        var r = ctx.getValue(getKindPath());
        if (r == null) {
            r = getDefaultKind();
            if (r == null) {
                throw new BadStateException("%s is required", getKindPath());
            }
        }
        return r;
    }

}
