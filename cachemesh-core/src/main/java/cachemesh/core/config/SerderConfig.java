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

import cachemesh.common.config3.annotations.Property;
import cachemesh.common.config3.annotations.Default;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.common.misc.Serderializer;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class SerderConfig {

    public static enum Kind {
        jackson(JacksonSerderializer.DEFAULT);

        public final Serderializer instance;

        private Kind(Serderializer instance) {
            this.instance = instance;
        }
    }

    @Builder.Default
    @Property @Default("jackson")
    private Kind kind = Kind.jackson;

}
