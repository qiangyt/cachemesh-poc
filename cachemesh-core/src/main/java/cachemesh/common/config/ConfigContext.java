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
package cachemesh.common.config;

import java.util.Map;

import cachemesh.common.config.suppport.ChildContext;
import cachemesh.common.misc.ClassCache;

public interface ConfigContext {

    ClassCache getClassCache();

    TypeRegistry getTypeRegistry();

    Object getValue(Path relative);

    Object setValue(Path relative, Object newValue);

    Map<String, Object> getRootValue();

    Path getPath();

    ConfigContext getParent();

    ConfigContext getRoot();

    default ConfigContext createChild(String name) {
        return new ChildContext(this, name);
    }

    default ConfigContext createChild(int index) {
        return new ChildContext(this, index);
    }

}
