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
package cachemesh.core.config.support;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.config.types.KindPathingDynamicBeanType;
import cachemesh.core.config.InlineMembersConfig;
import cachemesh.core.config.MembersConfig;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

public class MembersConfigType extends KindPathingDynamicBeanType<MembersConfig> {

    public MembersConfigType(@Nonnull TypeRegistry typeRegistry) {
        super(typeRegistry, MembersConfig.class, Path.of("./kind"), null);
    }

    @Nonnull
    public Map<String, BeanType<? extends MembersConfig>> createMapping(@Nonnull TypeRegistry typeRegistry) {
        checkNotNull(typeRegistry);

        var r = new HashMap<String, BeanType<? extends MembersConfig>>();
        r.put(InlineMembersConfig.KIND, InlineMembersConfig.of(typeRegistry));
        return r;
    }

}
