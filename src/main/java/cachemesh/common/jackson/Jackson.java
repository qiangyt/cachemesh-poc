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

import cachemesh.common.err.RequestException;
import cachemesh.common.util.StringHelper;

@lombok.Getter
@ThreadSafe
public class Jackson {

	public static final Jackson DEFAULT = new Jackson(buildDefaultMapper());

	public final ObjectMapper mapper;

	public Jackson(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static ObjectMapper buildDefaultMapper() {
		var r = new ObjectMapper();
		initDefaultMapper(r);
		return r;
	}

	public static void initDefaultMapper(ObjectMapper mapper) {
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

	public <T> T from(String text, Class<T> clazz) {
		if (StringHelper.isBlank(text)) {
			return null;
		}

		try {
			return getMapper().readValue(text, clazz);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public <T> T from(ByteString buf, Class<T> clazz) {
		if (buf == null) {
			return null;
		}

		try {
			return getMapper().readValue(buf.toByteArray(), clazz);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public <T> T from(ByteBuffer buf, Class<T> clazz) {
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
			throw new RequestException(e);
		}
	}

	public <T> T from(byte[] bytes, Class<T> clazz) {
		if (bytes == null) {
			return null;
		}

		try {
			return getMapper().readValue(bytes, clazz);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public <T> T from(String text, TypeReference<T> typeReference) {
		if (StringHelper.isBlank(text)) {
			return null;
		}

		try {
			return getMapper().readValue(text, typeReference);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public <T> T from(ByteString buf, TypeReference<T> typeReference) {
		if (buf == null) {
			return null;
		}

		try {
			return getMapper().readValue(buf.toByteArray(), typeReference);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public <T> T from(ByteBuffer buf, TypeReference<T> typeReference) {
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
			throw new RequestException(e);
		}
	}

	public <T> T from(byte[] bytes, TypeReference<T> typeReference) {
		if (bytes == null) {
			return null;
		}

		try {
			return getMapper().readValue(bytes, typeReference);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public String pretty(Object object) {
		return toString(object, true);
	}

	public String toString(Object object) {
		return toString(object, false);
	}

	public byte[] toBytes(Object object) {
		return toBytes(object, false);
	}

	public ByteString toByteString(Object object) {
		return toByteString(object, false);
	}

	public ByteBuffer toByteBuffer(Object object) {
		return toByteBuffer(object, false);
	}

	public String toString(Object object, boolean pretty) {
		if (object == null) {
			return null;
		}

		try {
			if (pretty) {
				return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
			}
			return getMapper().writeValueAsString(object);
		} catch (IOException e) {
			throw new RequestException(e);
		}
	}

	public ByteString toByteString(Object object, boolean pretty) {
		if (object == null) {
			return null;
		}
		return ByteString.copyFrom(toByteBuffer(object, pretty));
	}

	public byte[] toBytes(Object object, boolean pretty) {
		if (object == null) {
			return null;
		}
		return toByteBuffer(object, pretty).array();
	}

	public ByteBuffer toByteBuffer(Object object, boolean pretty) {
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
			throw new RequestException(e);
		}
	}
}
