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

import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

import cachemesh.common.config.ConvertContext;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
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
    @SuppressWarnings("all")
    public Object setValue(Path relative, Object newValue) {
        return getCachedValues().compute(relative, (k, oldValue) -> {
            var chain = normalizeChain(relative);

            Object r, owner = null;
            r = getRootValue();

            Path valuePath = null;

            for (var p : chain) {
                valuePath = p;
                owner = r;

                if (p.isIndex()) {
                    if (owner instanceof List) {
                        r = ((List) owner).get(p.getIndex());
                        continue;
                    }
                    if (owner.getClass().isArray()) {
                        r = Array.get(owner, p.getIndex());
                        continue;
                    }

                    var msg = String
                            .format("unable to get %s because corresponding value is neither a list nor an array", p);
                    throw new IllegalArgumentException(msg);
                }

                if (owner instanceof Map) {
                    r = ((Map) owner).get(p.getName());
                    continue;
                }

                var msg = String.format("unable to get %s because corresponding value is not a map", p);
                throw new IllegalArgumentException(msg);
            }

            if (valuePath.isIndex()) {
                if (owner instanceof List) {
                    ((List) owner).set(valuePath.getIndex(), newValue);
                } else if (owner.getClass().isArray()) {
                    Array.set(owner, valuePath.getIndex(), newValue);
                } else {
                    var msg = String.format(
                            "unable to set %s because corresponding value is neither a list nor an array", valuePath);
                    throw new IllegalArgumentException(msg);
                }
            } else if (owner instanceof Map) {
                ((Map) owner).put(valuePath.getName(), newValue);
            }

            return newValue;
        });
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

            r.offerLast(p);
        }

        return r;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getValue(Path relative) {
        return getCachedValues().computeIfAbsent(relative, k -> {
            var chain = normalizeChain(relative);

            Object r;
            r = getRootValue();

            for (var p : chain) {
                if (p.isIndex()) {
                    if (r instanceof List) {
                        r = ((List) r).get(p.getIndex());
                        continue;
                    }
                    if (r.getClass().isArray()) {
                        r = Array.get(r, p.getIndex());
                        continue;
                    }

                    var msg = String
                            .format("unable to get %s because corresponding value is neither a list nor an array", p);
                    throw new IllegalArgumentException(msg);
                }

                if (r instanceof Map) {
                    r = ((Map) r).get(p.getName());
                    continue;
                }

                var msg = String.format("unable to get %s because corresponding value is not a map", p);
                throw new IllegalArgumentException(msg);
            }

            return r;
        });
    }

}
