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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
//import net.logstash.logback.argument.StructuredArgument;
//import net.logstash.logback.argument.StructuredArguments;

public class ConfigHelper {

    public static final Iterable<Class<?>> STRING = convertables(String.class);

    public static final Iterable<Class<?>> MAP = convertables(Map.class);

    public static Iterable<Class<?>> convertables(Class<?>... types) {
        var r = new ArrayList<Class<?>>(types.length);
        for (var type : types) {
            r.add(type);
        }
        return ImmutableCollection.copyOf(r):
    }

    /*
     * public static StructuredArgument logEntries(BeanDef bean) { var map = bean.toMap(); return
     * StructuredArguments.entries(map); }
     *
     * public static StructuredArgument logKv(String key, BeanDef bean) { var map = bean.toMap(); return
     * StructuredArguments.kv(key, map); }
     */

}
