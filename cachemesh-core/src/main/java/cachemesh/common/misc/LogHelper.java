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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cachemesh.common.config.Bean;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;

public class LogHelper {

    public static Logger getLogger(Class<?> klass, String name) {
        return LoggerFactory.getLogger(name + "@" + klass.getCanonicalName());
    }

    public static StructuredArgument entries(Bean bean) {
        var map = bean.toMap();
        return StructuredArguments.entries(map);
    }

    public static StructuredArgument kv(String key, Bean bean) {
        var map = bean.toMap();
        return StructuredArguments.kv(key, map);
    }

}
