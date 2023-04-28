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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cachemesh.common.config.ConfigContext;
import cachemesh.common.config.Path;
import cachemesh.common.config.TypeRegistry;
import cachemesh.common.err.BadValueException;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;
import static com.google.common.base.Preconditions.*;

@Getter
public class RootContext extends AbstractContext {

    private final Map<Path, Object> cachedValues = new HashMap<>();

    public RootContext(@Nonnull ClassCache classCache, @Nonnull TypeRegistry typeRegistry,
            @Nonnull Map<String, Object> rootValue) {
        super(classCache, typeRegistry, rootValue);
    }

    @Override
    @Nonnull
    public Path getPath() {
        return Path.ROOT;
    }

    @Override
    @Nullable
    public ConfigContext getParent() {
        return null;
    }

    @Override
    @Nonnull
    public ConfigContext getRoot() {
        return this;
    }

    @Override
    @SuppressWarnings("all")
    @Nullable
    public Object setValue(@Nonnull Path relative, @Nullable Object newValue) {
        checkNotNull(relative);

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

                    throw new BadValueException(
                            "unable to get %s because corresponding value is neither a list nor an array", p);
                }

                if (owner instanceof Map) {
                    r = ((Map) owner).get(p.getName());
                    continue;
                }

                throw new BadValueException("unable to get %s because corresponding value is not a map", p);
            }

            if (valuePath.isIndex()) {
                if (owner instanceof List) {
                    ((List) owner).set(valuePath.getIndex(), newValue);
                } else if (owner.getClass().isArray()) {
                    Array.set(owner, valuePath.getIndex(), newValue);
                } else {
                    throw new BadValueException(
                            "unable to set %s because corresponding value is neither a list nor an array", valuePath);
                }
            } else if (owner instanceof Map) {
                ((Map) owner).put(valuePath.getName(), newValue);
            }

            return newValue;
        });
    }

    @Nonnull
    Deque<Path> normalizeChain(@Nonnull Path relative) {
        checkNotNull(relative);

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
                    throw new BadValueException("%s specifies invalid path scope", relative);
                }
            }

            r.offerLast(p);
        }

        return r;
    }

    @Override
    @SuppressWarnings("rawtypes")
    @Nullable
    public Object getValue(@Nonnull Path relative) {
        checkNotNull(relative);

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

                    throw new BadValueException(
                            "unable to get %s because corresponding value is neither a list nor an array", p);
                }

                if (r instanceof Map) {
                    r = ((Map) r).get(p.getName());
                    continue;
                }

                throw new BadValueException("unable to get %s because corresponding value is not a map", p);
            }

            return r;
        });
    }

}
