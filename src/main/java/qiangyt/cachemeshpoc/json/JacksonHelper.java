package qiangyt.cachemeshpoc.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import qiangyt.cachemeshpoc.err.CacheMeshInternalException;
import qiangyt.cachemeshpoc.err.CacheMeshRequestException;
import qiangyt.cachemeshpoc.util.StringHelper;

@lombok.Getter
public class JacksonHelper {

	public static final JacksonHelper DEFAULT = new JacksonHelper(buildMapper());

	public final ObjectMapper mapper;

	public JacksonHelper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static ObjectMapper buildMapper() {
		var r = new ObjectMapper();
		initMapper(r);
		return r;
	}

	public static void initMapper(ObjectMapper mapper) {
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
			throw new CacheMeshRequestException(e);
		}
	}

	public <T> T from(String text, TypeReference<T> typeReference) {
		if (StringHelper.isBlank(text)) {
			return null;
		}

		try {
			return getMapper().readValue(text, typeReference);
		} catch (IOException e) {
			throw new CacheMeshRequestException(e);
		}
	}

	public String pretty(Object object) {
		return to(object, true);
	}

	public String to(Object object) {
		return to(object, false);
	}

	public String to(Object object, boolean pretty) {
		if (object == null) {
			return null;
		}

		try {
			if (pretty) {
				return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
			}
			return getMapper().writeValueAsString(object);
		} catch (IOException e) {
			throw new CacheMeshRequestException(e);
		}
	}
}
