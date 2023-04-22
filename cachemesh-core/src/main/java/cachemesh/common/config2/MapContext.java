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
package cachemesh.common.config2;

import java.util.Map;

import cachemesh.common.misc.ClassCache;

public class MapContext {

    private final ClassLoader classLoader;

    private final ClassCache classCache;

    private final Map<String, Object> root;

    public MapContext(Map<String, Object> root) {
        this(Thread.currentThread().getContextClassLoader(), ClassCache.DEFAULT, root);
    }

    public MapContext(ClassLoader classLoader, ClassCache classCache, Map<String, Object> root) {
        this.classLoader = classLoader;
        this.classCache = classCache;
        this.root = root;
    }

    public ClassLoader classLoader() {
        return this.classLoader;
    }

    public ClassCache classCache() {
        return this.classCache;
    }

    public Class<?> resolveClass(String className) {
        return classCache().resolve(classLoader(), className);
    }

    public final Map<String, Object> root() {
        return this.root;
    }

}
