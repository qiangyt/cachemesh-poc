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

import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

import cachemesh.common.config3.ConvertContext;
import cachemesh.common.config3.Path;
import cachemesh.common.config3.TypeRegistry;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

@Getter
public class RootConvertContext extends AbstractConvertContext {

    private final Map<Path, Object> cachedValues = new HashMap<>();

    public RootConvertContext(ClassCache classCache, TypeRegistry typeRegistry, Map<String, Object> rootValue) {
        super(classCache, typeRegistry, rootValue);
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
    public Object setValue(Path relative, Object newValue) {
        var chain = normalizeChain(relative);
        Object r = locateValue(chain);

        var owner = chain.removeLast();

        return r;
    }
    
    Deque<Path> normalizeChain(Path relative) {
        var chain = getPath().toChain();
        chain.addAll(relative.toChain());

        Deque<Path> r = new ArrayDeque<>();

        for (var p : chain) {
            if (p.isKeep()) {
                continue;
            }

            if (p.isUpward()) {
                try {
                    r.removeLast();
                    continue;
                } catch (NoSuchElementException e) {
                    var msg = String.format("%s specifies invalid path scope");
                    throw new IllegalArgumentException(msg);
                }
            }
        }
        
        return r;
    }

    @SuppressWarnings("rawtypes")
    private Object locateValue(Collection<Path> normalizedChain) {
        Object r;
        r = getRootValue();

        Deque<Object> upper = new ArrayDeque<>();

        for (var p : normalizedChain) {
            if (p.isKeep()) {
                continue;
            }

            if (p.isUpward()) {
                try {
                    r = upper.removeLast();
                    continue;
                } catch (NoSuchElementException e) {
                    var msg = String.format("%s specifies invalid path scope");
                    throw new IllegalArgumentException(msg);
                }
            }

            if (p.isIndex()) {
                if (r instanceof List) {
                    upper.offerLast(r);
                    r = ((List)r).get(p.getIndex());
                    continue;
                }
                if (r.getClass().isArray()) {
                    upper.offerLast(r);
                    r = Array.get(r, p.getIndex());
                    continue;
                } 
                
                var msg = String.format("unable to get %s because corresponding value is not a list or an array", p);
                throw new IllegalArgumentException(msg);
            }

            var name = p.getName();
            if (r instanceof Map) {
                upper.offerLast(r);
                r = ((Map)r).get(name);
                continue;
            }


            var msg = String.format("unable to get %s because corresponding value is not a map", p);
            throw new IllegalArgumentException(msg);
        }
        
        return r;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getValue(Path relative) {
        return getCachedValues().computeIfAbsent(relative, k -> {
            var chain = normalizeChain(relative);
            return locateValue(chain);
        });
    }

}
