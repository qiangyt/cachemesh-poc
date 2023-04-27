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
package cachemesh.common.config.suppport;

import java.util.Map;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

@Getter
public abstract class AbstractContext implements ConfigContext {

    private final ClassCache classCache;

    private final TypeRegistry typeRegistry;

    private final Map<String, Object> rootValue;

    public AbstractContext(ClassCache classCache, TypeRegistry typeRegistry, Map<String, Object> rootValue) {
        this.classCache = classCache;
        this.typeRegistry = typeRegistry;
        this.rootValue = rootValue;
    }

    @Override
    public Object getValue(Path relative) {
        return getRoot().getValue(relative);
    }

    @Override
    public Object setValue(Path relative, Object newValue) {
        return getRoot().setValue(relative, newValue);
    }

}
