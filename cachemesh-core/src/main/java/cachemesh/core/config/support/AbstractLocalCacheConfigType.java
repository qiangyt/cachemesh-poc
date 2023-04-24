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

import java.util.Map;

import cachemesh.common.config3.Prop;
import cachemesh.common.config3.types.BeanType;
import cachemesh.core.config.LocalCacheConfig;

public abstract class AbstractLocalCacheConfigType extends BeanType<LocalCacheConfig> {

    public AbstractLocalCacheConfigType() {
        super(LocalCacheConfig.class);
    }

    @Override
    public LocalCacheConfig newInstance(Object indicator) {
        var type = determineConcreteType(indicator);
        return type.newInstance(indicator);
    }

    public abstract BeanType<? extends LocalCacheConfig> determineConcreteType(Object indicator);

    @Override
    public Map<String, Prop<?, ?>> getProperties(Object indicator) {
        var type = determineConcreteType(indicator);
        return type.getProperties(indicator);
    }

}
