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
package cachemesh.common.config.types;

import java.time.Duration;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.suppport.AbstractType;
import cachemesh.common.config.suppport.ConfigHelper;
import cachemesh.common.misc.DurationHelper;
import lombok.Getter;

@Getter
public class DurationType extends AbstractType<Duration> {

    public static final DurationType DEFAULT = new DurationType();

    @Override
    public Class<?> getKlass() {
        return Duration.class;
    }

    @Override
    public Iterable<Class<?>> convertableClasses() {
        return ConfigHelper.STRING;
    }

    @Override
    protected Duration doConvert(ConfigContext ctx, Object value) {
        return DurationHelper.parse((String) value);
    }

}
