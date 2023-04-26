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
package cachemesh.core.config;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cachemesh.common.config3.annotations.PropertyElement;
import cachemesh.common.misc.Dumpable;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
public class LocalConfig implements Dumpable {

    public static final String DEFAULT_KIND = "caffeine";

    private String kind = DEFAULT_KIND;

    private LocalCacheConfig defaultCache;

    @Singular("cache")
    @PropertyElement(LocalCacheConfig.class)
    private List<LocalCacheConfig> caches;

    protected LocalConfig(LocalCacheConfig defaultCache, List<LocalCacheConfig> caches) {
        this.kind = defaultCache.getName();
        this.defaultCache = defaultCache;
        this.caches = caches;
    }

    @Override
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();

        r.put("kind", getKind());
        r.put("defaultCache", getDefaultCache().toMap());
        r.put("caches", Dumpable.toMap(getCaches()));

        return r;
    }

}
