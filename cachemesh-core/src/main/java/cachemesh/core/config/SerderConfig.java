/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Map;
import java.util.Collection;

import cachemesh.common.Serderializer;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.common.config.EnumAccessor;
import cachemesh.common.config.Accessor;
import cachemesh.common.config.SomeConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SerderConfig implements SomeConfig {

    public static enum Kind {
                             jackson
    }

    public static final SerderConfig            DEFAULT               = new SerderConfig();

    public static final Kind                    DEFAULT_KIND          = Kind.jackson;
    private Kind                                kind;

    public static final Serderializer           DEFAULT_SERDERIALIZER = JacksonSerderializer.DEFAULT;
    private Serderializer                       instance              = DEFAULT_SERDERIALIZER;

    public static final Collection<Accessor<?>> ACCESSORS             = SomeConfig
        .buildAccessors(new EnumAccessor<Kind>(SerderConfig.class, Kind.class, "kind", Kind.jackson));

    @Override
    public Collection<Accessor<?>> accessors() {
        return ACCESSORS;
    }

}
