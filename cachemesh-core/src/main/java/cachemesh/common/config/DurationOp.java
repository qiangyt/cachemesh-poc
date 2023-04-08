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
package cachemesh.common.config;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cachemesh.common.misc.DurationHelper;
import lombok.Getter;

@Getter
public class DurationOp implements Operator<Duration> {

    public static final DurationOp DEFAULT = new DurationOp();

    public static final Collection<Class<?>> CONVERTABLE_CLASSES = Collections
            .unmodifiableCollection(List.of(String.class));

    @Override
    public Class<?> propertyClass() {
        return Duration.class;
    }

    @Override
    public Collection<Class<?>> convertableClasses() {
        return CONVERTABLE_CLASSES;
    }

    @Override
    public Duration doConvert(String hint, Object value) {
        return DurationHelper.parse((String) value);
    }

}
