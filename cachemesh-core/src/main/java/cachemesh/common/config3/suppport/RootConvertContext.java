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

import java.util.Map;

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.Path;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

@Getter
public class RootConvertContext extends AbstractConvertContext {

    private final Map<String, Object> rootValue;

    public RootConvertContext(ClassCache classCache, TypeRegistry typeRegistry, Map<String, Object> rootValue) {
        super(classCache, typeRegistry);

        this.rootValue = rootValue;
    }

    @Override
    public Path getPath() {
        return Path.ROOT;
    }

    @Override
    public ConvertContext getParent() {
        return null;
    }

    @Override
    public ConvertContext getRoot() {
        return this;
    }

    @Override
    public Object getValue(Path childPath) {
        if (childPath.isUpward()) {
            throw new IllegalStateException("cannot traverse upward on root path: " + childPath);
        }

        if (childPath.isAbsolute() == false) {
            childPath = childPath.toAbsolute(getPath());
        }

        childPath.getName();
        path = path.toAbsolute(getPath());
    }

}
