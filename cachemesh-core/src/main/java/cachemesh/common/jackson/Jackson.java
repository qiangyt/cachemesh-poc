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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.ByteString;

import cachemesh.common.err.BadStateException;
import cachemesh.common.misc.StringHelper;
import lombok.Getter;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import static com.google.common.base.Preconditions.*;

@Getter
@ThreadSafe
public class Jackson {

    public static final Jackson DEFAULT = new Jackson(buildDefaultMapper());

    @Nonnull
    public final ObjectMapper mapper;

    public Jackson(@Nonnull ObjectMapper mapper) {
        this.mapper = checkNotNull(mapper);
    }

    @Nonnull
    public static ObjectMapper buildDefaultMapper() {
        var r = new ObjectMapper();
        initDefaultMapper(r);
        return r;
    }

    public static void initDefaultMapper(@Nonnull ObjectMapper mapper) {
        checkNotNull(mapper);

        var dateModule = new SimpleModule();
        dateModule.addSerializer(Date.class, new DateSerializer());
        dateModule.addDeserializer(Date.class, new DateDeserialize());
        mapper.registerModule(dateModule);

        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    }

    @Nullable
    public <T> T from(@Nullable String text, @Nonnull Class<T> clazz) {
        checkNotNull(clazz);

        if (StringHelper.isBlank(text)) {
            return null;
        }

        try {
            return getMapper().readValue(text, clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable ByteString buf, @Nonnull Class<T> clazz) {
        checkNotNull(clazz);

        if (buf == null) {
            return null;
        }

        try {
            return getMapper().readValue(buf.toByteArray(), clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable ByteBuffer buf, @Nonnull Class<T> clazz) {
        checkNotNull(clazz);

        if (buf == null) {
            return null;
        }

        try {
            if (!buf.hasArray()) {
                byte[] bytes = new byte[buf.remaining()];
                buf.get(bytes);
                return getMapper().readValue(bytes, clazz);
            }

            final int offset = buf.arrayOffset();
            return getMapper().readValue(buf.array(), offset + buf.position(), buf.remaining(), clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable byte[] bytes, @Nonnull Class<T> clazz) {
        checkNotNull(clazz);

        if (bytes == null) {
            return null;
        }

        try {
            return getMapper().readValue(bytes, clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable String text, @Nonnull TypeReference<T> typeReference) {
        checkNotNull(typeReference);

        if (StringHelper.isBlank(text)) {
            return null;
        }

        try {
            return getMapper().readValue(text, typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable ByteString buf, @Nonnull TypeReference<T> typeReference) {
        checkNotNull(typeReference);

        if (buf == null) {
            return null;
        }

        try {
            return getMapper().readValue(buf.toByteArray(), typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable ByteBuffer buf, @Nonnull TypeReference<T> typeReference) {
        checkNotNull(typeReference);

        if (buf == null) {
            return null;
        }

        try {
            if (!buf.hasArray()) {
                byte[] bytes = new byte[buf.remaining()];
                buf.get(bytes);
                return getMapper().readValue(bytes, typeReference);
            }

            final int offset = buf.arrayOffset();
            return getMapper().readValue(buf.array(), offset + buf.position(), buf.remaining(), typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable byte[] bytes, @Nonnull TypeReference<T> typeReference) {
        checkNotNull(typeReference);

        if (bytes == null) {
            return null;
        }

        try {
            return getMapper().readValue(bytes, typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public String pretty(@Nullable Object object) {
        return toString(object, true);
    }

    @Nullable
    public String toString(@Nullable Object object) {
        return toString(object, false);
    }

    @Nullable
    public byte[] toBytes(@Nullable Object object) {
        return toBytes(object, false);
    }

    @Nullable
    public ByteString toByteString(@Nullable Object object) {
        return toByteString(object, false);
    }

    @Nullable
    public ByteBuffer toByteBuffer(@Nullable Object object) {
        return toByteBuffer(object, false);
    }

    @Nullable
    public String toString(@Nullable Object object, boolean pretty) {
        if (object == null) {
            return null;
        }

        try {
            if (pretty) {
                return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return getMapper().writeValueAsString(object);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public ByteString toByteString(@Nullable Object object, boolean pretty) {
        if (object == null) {
            return null;
        }
        return ByteString.copyFrom(toByteBuffer(object, pretty));
    }

    @Nullable
    public byte[] toBytes(@Nullable Object object, boolean pretty) {
        if (object == null) {
            return null;
        }
        return toByteBuffer(object, pretty).array();
    }

    @Nullable
    public ByteBuffer toByteBuffer(@Nullable Object object, boolean pretty) {
        if (object == null) {
            return null;
        }

        try {
            var buf = new ByteArrayBuilder();

            if (pretty) {
                getMapper().writerWithDefaultPrettyPrinter().writeValue(buf, object);
            } else {
                getMapper().writeValue(buf, object);
            }

            return ByteBuffer.wrap(buf.getCurrentSegment(), 0, buf.getCurrentSegmentLength());
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }
}
