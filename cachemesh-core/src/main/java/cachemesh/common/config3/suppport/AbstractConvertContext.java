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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.Path;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

@Getter
public abstract class AbstractConvertContext implements ConvertContext {

    private final ClassCache classCache;

    private final TypeRegistry typeRegistry;
    
    private final Map<String, Object> rootValue;

    public AbstractConvertContext(ClassCache classCache, TypeRegistry typeRegistry, Map<String, Object> rootValue) {
        this.classCache = classCache;
        this.typeRegistry = typeRegistry;
        this.rootValue = rootValue;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getValue(Path relative) {
        var chain = getPath().toChain();
        chain.addAll(relative.toChain());

        Deque<Object> upper = new ArrayDeque<>();
        Object current;
        current = getRootValue();

        for (var p : chain) {
            if (p.isKeep()) {
                continue;
            }

            if (p.isUpward()) {
                try {
                    current = upper.removeLast();
                } catch (NoSuchElementException e) {
                    var msg = String.format("%s specifies invalid path scope");
                    throw new IllegalArgumentException(msg);
                }
                continue;
            }

            var name = p.getName();
            if (current instanceof Map) {
                upper.offerLast(current);
                current = ((Map)current).get(name);
            } else {
                var msg = String.format("unable to get %s because corresponding value is not a map", p);
                throw new IllegalArgumentException(msg);
            }
        }
        
        return current;
    }

}
