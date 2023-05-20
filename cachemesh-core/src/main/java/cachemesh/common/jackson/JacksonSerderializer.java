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
package cachemesh.common.jackson;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cachemesh.common.misc.Serderializer;
import lombok.Getter;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
public class JacksonSerderializer implements Serderializer {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final JacksonSerderializer DEFAULT = new JacksonSerderializer();

    @Nonnull
    private final Jackson jackson;

    @Nonnull
    private final Charset charset;

    public JacksonSerderializer() {
        this(Jackson.DEFAULT, DEFAULT_CHARSET);
    }

    public JacksonSerderializer(@Nonnull Jackson jackson, @Nonnull Charset charset) {
        this.jackson = checkNotNull(jackson);
        this.charset = checkNotNull(charset);
    }

    @Override
    @Nullable
    public byte[] serialize(@Nonnull Object obj) {
        return this.jackson.toBytes(obj);
    }

    @Override
    @Nullable
    public <T> T deserialize(@Nonnull byte[] bytes, @Nonnull Class<T> clazz) {
        return this.jackson.from(bytes, clazz);
    }

}
