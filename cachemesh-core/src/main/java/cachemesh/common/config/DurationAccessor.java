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
package cachemesh.common.config;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class DurationAccessor extends Accessor<Duration> {

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(String.class));

    private final Duration defaultValue;

    public DurationAccessor(Class<?> ownerClass, String name, Duration defaultValue) {
        super(ownerClass, name);
        this.defaultValue = defaultValue;
    }

    @Override
    public Duration defaultValue() {
        return this.defaultValue;
    }

    @Override
    public Class<?> propertyClass() {
        return Duration.class;
    }

    @Override
    public Duration createEmptyValue() {
        return defaultValue();
    }

    @Override
    public Collection<Class<?>> convertableClasses() {
        return CONVERTABLE_CLASSES;
    }

    @Override
    public Duration doConvert(String hint, Object value) {
        return Duration.parse((String) value);
    }

}
