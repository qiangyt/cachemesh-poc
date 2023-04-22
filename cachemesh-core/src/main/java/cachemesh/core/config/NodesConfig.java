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

import cachemesh.common.config2.AbstractProp;
import cachemesh.common.config2.Prop;
import cachemesh.common.config2.types.StringType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NodesConfig {

    public static final Prop<String> KIND_PROP = new AbstractProp("kind", StringType.DEFAULT, "inline") {

        @Override
        public void set(Object bean, Object value) {

        }

    };

    private String kind;

}
