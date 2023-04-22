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

import cachemesh.common.config.ReflectProp;
import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Prop;
import cachemesh.common.config.Bean;
import cachemesh.common.config.op.ClassOp;
import cachemesh.common.config.op.StringOp;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class LocalCacheConfig implements Bean {

    public static final Prop<String> NAME_PROP = ReflectProp.<String> builder().config(LocalCacheConfig.class)
            .name("name").op(StringOp.DEFAULT).build();

    public static final Prop<Class<?>> VALUE_CLASS_PROP = ReflectProp.<Class<?>> builder()
            .config(LocalCacheConfig.class).name("valueClass").devault(byte[].class).op(ClassOp.DEFAULT).build();

    public static final Prop<SerderConfig> SERDER_PROP = ReflectProp.<SerderConfig> builder()
            .config(LocalCacheConfig.class).name("serder").devault(SerderConfig.builder().build()).op(SerderConfig.OP)
            .build();

    protected static final Iterable<Prop<?>> PROPS = ConfigHelper.props(NAME_PROP, VALUE_CLASS_PROP, SERDER_PROP);

    private String name;

    private Class<?> valueClass;

    @Builder.Default
    private SerderConfig serder = SerderConfig.builder().build();

    protected LocalCacheConfig() {
    }

    protected LocalCacheConfig(String name, Class<?> valueClass, SerderConfig serder) {
        this.name = name;
        this.valueClass = valueClass;
        this.serder = serder;
    }

    public abstract LocalCacheConfig buildAnother(String name, Class<?> valueClass);

    @Override
    public Iterable<Prop<?>> props() {
        return PROPS;
    }

}
