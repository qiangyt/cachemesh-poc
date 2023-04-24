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

import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.config3.annotations.PropertyElement;
import cachemesh.common.config3.reflect.ReflectBeanType;
import cachemesh.common.config3.types.BeanType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
public class InlineMembersConfig extends MembersConfig {

    public static final String KIND = "inline";

    @Singular(KIND)
    @PropertyElement(NodeConfig.class)
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

    public static BeanType<InlineMembersConfig> of(TypeRegistry typeRegistry) {
        return ReflectBeanType.of(typeRegistry, InlineMembersConfig.class);
    }

}
