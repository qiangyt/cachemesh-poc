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
package cachemesh.lettuce;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import io.lettuce.core.codec.RedisCodec;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.google.common.base.Preconditions.*;

@Getter
public class LettuceCodec implements RedisCodec<String, byte[]> {

    public static final LettuceCodec DEFAULT = new LettuceCodec(StandardCharsets.UTF_8);

    @Nonnull
    private final Charset charset;

    public LettuceCodec(@Nonnull Charset charset) {
        checkNotNull(charset);
        this.charset = charset;
    }

    @Override
    @Nonnull
    public String decodeKey(@Nonnull ByteBuffer bytes) {
        checkNotNull(bytes);

        if (!bytes.hasArray()) {
            bytes = bytes.get(new byte[bytes.remaining()]);
        }

        final int offset = bytes.arrayOffset();
        return new String(bytes.array(), offset + bytes.position(), bytes.remaining(), getCharset());
    }

    @Override
    @Nonnull
    public byte[] decodeValue(@Nonnull ByteBuffer bytes) {
        checkNotNull(bytes);

        byte[] r = new byte[bytes.remaining()];
        bytes.get(r);
        return r;
    }

    @Override
    @Nonnull
    public ByteBuffer encodeKey(@Nonnull String key) {
        checkNotNull(key);

        return ByteBuffer.wrap(key.getBytes(getCharset()));
    }

    @Override
    @Nonnull
    public ByteBuffer encodeValue(@Nullable byte[] value) {
        return ByteBuffer.wrap(value);
    }

}
