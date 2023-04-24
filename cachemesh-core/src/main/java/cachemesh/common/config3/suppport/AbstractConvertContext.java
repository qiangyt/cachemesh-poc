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
package cachemesh.common.config3.suppport;

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

@Getter
public abstract class AbstractConvertContext implements ConvertContext {

    private final ClassCache classCache;

    private final TypeRegistry typeRegistry;

    public AbstractConvertContext(ClassCache classCache, TypeRegistry typeRegistry) {
        this.classCache = classCache;
        this.typeRegistry = typeRegistry;
    }

}
