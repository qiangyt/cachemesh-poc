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
package cachemesh.common.registry;

import java.util.HashMap;
import java.util.function.Function;

import cachemesh.common.err.BadValueException;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class Registry<KIND, VALUE> {

    @Getter(AccessLevel.PROTECTED)
    @Nonnull
    private final Map<KIND, VALUE> localMap = new HashMap<>();

    @Getter
    @Nullable
    private final Registry<KIND, VALUE> parent;

    protected Registry() {
        this(null);
    }

    protected Registry(@Nullable Registry<KIND, VALUE> parent) {
        this.parent = parent;
    }

    @Nonnull
    public abstract String getValueName();

    @Nonnull
    public Map<KIND, VALUE> getAll() {
        var p = getParent();
        if (p == null) {
            return ImmutableMap.copyOf(getLocalMap());
        }

        var r = new HashMap<KIND, VALUE>();
        r.putAll(p.getAll());
        r.putAll(getLocalMap());
        return ImmutableMap.copyOf(r);
    }

    public void register(@Nonnull KIND kind, @Nonnull VALUE value) {
        requireNonNull(kind);
        requireNonNull(value);

        getLocalMap().compute(kind, (k, existing) -> {
            if (existing != null) {
                throw new BadValueException("duplicated %s: %s", getValueName(), kind);
            }
            return value;
        });
    }

    @Nonnull
    public VALUE resolve(@Nonnull KIND kind, @Nonnull Function<KIND, VALUE> creator) {
        requireNonNull(kind);
        requireNonNull(creator);

        var p = getParent();
        if (p != null) {
            var r = p.get(kind);
            if (r != null) {
                return r;
            }
        }

        return getLocalMap().computeIfAbsent(kind, k -> creator.apply(kind));
    }

    @Nullable
    public VALUE unregister(@Nonnull KIND kind) {
        requireNonNull(kind);

        return getLocalMap().remove(kind);
    }

    @Nonnull
    public VALUE load(@Nonnull KIND kind) {
        requireNonNull(kind);

        VALUE r = get(kind);
        if (r == null) {
            throw new BadValueException("unknown %s: %s", getValueName(), kind);
        }
        return r;
    }

    @Nullable
    public VALUE get(@Nonnull KIND kind) {
        requireNonNull(kind);

        var p = getParent();
        if (p != null) {
            var r = p.get(kind);
            if (r != null) {
                return r;
            }
        }

        return getLocalMap().get(kind);
    };

}
