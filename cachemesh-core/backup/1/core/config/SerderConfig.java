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

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.annotation.ADefault;
import cachemesh.common.annotation.AProperty;
import cachemesh.common.jackson.JacksonSerderializer;
import cachemesh.common.misc.Dumpable;
import cachemesh.common.misc.Serderializer;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
@Setter
@Builder
public class SerderConfig implements Dumpable {

    public static enum Kind {
        jackson(JacksonSerderializer.DEFAULT);

        @Nonnull
        public final Serderializer instance;

        private Kind(@Nonnull Serderializer instance) {
            checkNotNull(instance);
            this.instance = instance;
        }
    }

    @Builder.Default
    @AProperty
    @ADefault("jackson")
    @Nonnull
    private Kind kind = Kind.jackson;

    @Override
    @Nonnull
    public Map<String, Object> toMap() {
        var r = new HashMap<String, Object>();
        r.put("kind", getKind());
        return r;
    }

}
