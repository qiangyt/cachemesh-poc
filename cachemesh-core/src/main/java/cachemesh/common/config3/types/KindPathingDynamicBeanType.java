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
package cachemesh.common.config3.types;

import java.util.Map;

import cachemesh.common.config3.TypeRegistry;
import lombok.Getter;

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.Path;

@Getter
public abstract class KindPathingDynamicBeanType<T> extends DynamicBeanType<T> {

    private final Path kindPath;

    private final Object defaultKind;

    public KindPathingDynamicBeanType(TypeRegistry typeRegistry, Class<T> klass, Path kindPath, Object defaultKind) {
        super(typeRegistry, klass);
        this.kindPath = kindPath;
        this.defaultKind = defaultKind;
    }

    @Override
    public Object extractKind(ConvertContext ctx, Map<String, Object> propValues) {
        var r = ctx.getValue(getKindPath());
        if (r == null) {
            r = getDefaultKind();
            if (r == null) {
                var msg = String.format("%s is required", getKindPath());
                throw new IllegalStateException(msg);
            }
        }
        return r;
    }

}
