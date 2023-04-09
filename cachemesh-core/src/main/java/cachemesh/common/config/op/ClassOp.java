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
package cachemesh.common.config.op;

import java.util.Map;

import cachemesh.common.config.ConfigHelper;
import cachemesh.common.config.Op;
import cachemesh.common.misc.ClassCache;
import lombok.Getter;

@Getter
public class ClassOp implements Op<Class<?>> {

    public static final ClassOp DEFAULT = new ClassOp(ClassCache.DEFAULT);

    private final ClassCache cache;

    public ClassOp(ClassCache cache) {
        this.cache = cache;
    }

    @Override
    public Class<?> type() {
        return Class.class;
    }

    @Override
    public Iterable<Class<?>> convertableTypes() {
        return ConfigHelper.STRING;
    }

    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Class<?> convert(String hint, Map<String, Object> parent, Object value) {
        return this.cache.resolve(getClassLoader(), (String) value);
    }

}
