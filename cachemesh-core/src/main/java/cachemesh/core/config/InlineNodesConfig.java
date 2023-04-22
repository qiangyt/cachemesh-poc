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

import cachemesh.common.config2.AbstractProp;
import cachemesh.common.config2.AbstractType;
import cachemesh.common.config2.MapContext;
import cachemesh.common.config2.Mapper;
import cachemesh.common.config2.Path;
import cachemesh.common.config2.Type;
import cachemesh.common.config2.TypeRegistry;
import cachemesh.common.config2.annotations.Property;
import cachemesh.common.config2.annotations.PropertyElement;
import cachemesh.common.config2.reflect.ReflectDef;
import cachemesh.common.config2.reflect.ReflectProp;
import cachemesh.common.config2.reflect.ReflectMapper;
import cachemesh.common.misc.Reflect;
import cachemesh.common.misc.Serderializer;
import cachemesh.core.TransportRegistry;
import lombok.Singular;

public class InlineNodesConfig extends NodesConfig {

    @Singular("inline")
    @Property(ignore = true)
    private List<NodeConfig> inline;

    public static ReflectDef<InlineNodesConfig> buildDefinition(TypeRegistry typeRegistry) {
        var klass = InlineNodesConfig.class;
		var ctor = Reflect.defaultConstructor(klass);

        var nodeConfigType = new AbstractType<NodeConfig>(){

            @Override
            public Class<?> klass() {
                return NodeConfig.class;
            }

			@Override
			public NodeConfig doConvert(MapContext ctx, Path path, Object parent, Object value) {
				var c = (NodeConfig)parent;

			}

        };

        var props = ReflectProp.of(typeRegistry, klass);

        props.put("inline", );

		return new ReflectDef<SerderConfig>(klass, ctor, props);
    }

    public static Mapper<SerderConfig> buildMapper(TypeRegistry typeRegistry) {
        var def = buildDefinition(typeRegistry);
        return new ReflectMapper<>(def);
    }

}
