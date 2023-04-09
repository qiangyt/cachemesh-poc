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
package cachemesh.common.misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassCache {

    public static final ClassCache DEFAULT = new ClassCache();

    class Key {
        final ClassLoader loader;
        final String className;
        final int hash;

        Key(ClassLoader loader, String className) {
            this.loader = loader;
            this.className = className;
            this.hash = (this.loader.getName() + className).hashCode();
        }

        @Override
        public int hashCode() {
            return this.hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }

            Key that;
            try {
                that = (Key) obj;
            } catch (ClassCastException e) {
                return false;
            }

            return that.loader == this.loader && that.className.equals(this.className);
        }
    }

    private final Map<Key, Class<?>> cache = new ConcurrentHashMap<>();

    public int size() {
        return this.cache.size();
    }

    public Class<?> resolve(ClassLoader classLoader, String className) {
        return this.cache.computeIfAbsent(new Key(classLoader, className), key -> {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

}
