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

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import cachemesh.common.err.BadValueException;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class Registry<KIND, VALUE> {

    @Getter(AccessLevel.PROTECTED)
    private final Map<KIND, VALUE> localMap = new HashMap<>();

    @Getter
    private final Registry<KIND, VALUE> parent;

    protected Registry() {
        this(null);
    }

    protected Registry(Registry<KIND, VALUE> parent) {
        this.parent = parent;
    }

    public abstract String getValueName();

    public Map<KIND, VALUE> getAll() {
        var p = getParent();
        if (p == null) {
            return Collections.unmodifiableMap(getLocalMap());
        }

        var r = new HashMap<KIND, VALUE>();
        r.putAll(p.getAll());
        r.putAll(getLocalMap());
        return Collections.unmodifiableMap(r);
    }

    public void register(KIND kind, VALUE value) {
        getLocalMap().compute(kind, (k, existing) -> {
            if (existing != null) {
                throw new BadValueException("duplicated %s: %s", getValueName(), kind);
            }
            return value;
        });
    }

    public VALUE resolve(KIND kind, Function<KIND, VALUE> creator) {
        var p = getParent();
        if (p != null) {
            var r = p.get(kind);
            if (r != null) {
                return r;
            }
        }

        return getLocalMap().computeIfAbsent(kind, k -> creator.apply(kind));
    }

    public VALUE unregister(KIND kind) {
        return getLocalMap().remove(kind);
    }

    public VALUE load(KIND kind) {
        VALUE r = get(kind);
        if (r == null) {
            throw new BadValueException("unknown %s: %s", getValueName(), kind);
        }
        return r;
    }

    public VALUE get(KIND kind) {
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
