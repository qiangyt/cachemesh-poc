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
package cachemesh.core.config;

import java.util.List;
import java.util.Map;

import cachemesh.common.annotations.AElement;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.config.reflect.ReflectBeanType;
import cachemesh.common.config.types.BeanType;
import cachemesh.common.misc.Dumpable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
public class InlineMembersConfig extends MembersConfig {

    public static final String KIND = "inline";

    @Singular(KIND)
    @AElement(NodeConfig.class)
    private List<NodeConfig> inline;

    @Builder
    public InlineMembersConfig(List<NodeConfig> inline) {
        setKind(KIND);
        this.inline = inline;
    }

    public InlineMembersConfig() {
        setKind(KIND);
    }

    @Override
    public List<NodeConfig> nodes() {
        return getInline();
    }

    @Override
    public Map<String, Object> toMap() {
        var r = super.toMap();
        r.put("inilne", Dumpable.toMap(getInline()));
        return r;
    }

    @SuppressWarnings("unchecked")
    public static BeanType<InlineMembersConfig> of(TypeRegistry typeRegistry) {
        var r = typeRegistry.resolve(InlineMembersConfig.class, k -> {
            return ReflectBeanType.of(typeRegistry, InlineMembersConfig.class);
        });
        return (BeanType<InlineMembersConfig>) r;
    }

}
