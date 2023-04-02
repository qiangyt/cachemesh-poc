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
package cachemesh.core.spi;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.HasName;
import cachemesh.common.Serderializer;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LocalCacheConfig implements HasName {

    private final String name;

    private final Class<?> valueClass;

    private final Serderializer serder;

    // private final boolean cacheBytes;

    private final LocalCacheFactory factory;

    public LocalCacheConfig(String name, Class<?> valueClass, Serderializer serder, LocalCacheFactory factory) {
        this.name = name;
        this.valueClass = valueClass;
        this.serder = serder;
        this.factory = factory;
    }

    public LocalCacheConfig buildAnother(String name, Class<?> valueClass) {
        return new LocalCacheConfig(name, valueClass, getSerder(), getFactory());
    }

    @Override
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();

        r.put("name", getName());
        r.put("valueClass", getValueClass());
        r.put("serder", getSerder().toMap());
        r.put("factory", getFactory().toMap());

        return r;
    }

}
