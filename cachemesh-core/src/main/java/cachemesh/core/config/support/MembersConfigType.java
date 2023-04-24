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

import cachemesh.common.config3.Path;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.types.BeanType;
import cachemesh.common.config3.types.DynamicBeanType;
import cachemesh.core.config.InlineMembersConfig;
import cachemesh.core.config.MembersConfig;

public class MembersConfigType extends DynamicBeanType<MembersConfig> {

    public MembersConfigType(TypeRegistry typeRegistry) {
        super(typeRegistry, MembersConfig.class, Path.of("./kind"), null);
    }

    public Map<String, BeanType<? extends MembersConfig>> createMapping(TypeRegistry typeRegistry) {
        var r = new HashMap<String, BeanType<? extends MembersConfig>>();
        r.put(InlineMembersConfig.KIND, InlineMembersConfig.of(typeRegistry));
        return r;
    }

}
