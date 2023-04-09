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

@Getter
public class LettuceCodec implements RedisCodec<String, byte[]> {

    public static final LettuceCodec DEFAULT = new LettuceCodec(StandardCharsets.UTF_8);

    private final Charset charset;

    public LettuceCodec(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        if (!bytes.hasArray()) {
            bytes = bytes.get(new byte[bytes.remaining()]);
        }

        final int offset = bytes.arrayOffset();
        return new String(bytes.array(), offset + bytes.position(), bytes.remaining(), getCharset());
    }

    @Override
    public byte[] decodeValue(ByteBuffer bytes) {
        byte[] r = new byte[bytes.remaining()];
        bytes.get(r);
        return r;
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return ByteBuffer.wrap(key.getBytes(getCharset()));
    }

    @Override
    public ByteBuffer encodeValue(byte[] value) {
        return ByteBuffer.wrap(value);
    }

}
