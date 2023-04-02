package cachemesh.common.jackson;

import com.fasterxml.jackson.core.type.TypeReference;

public class JacksonHelper {

  public static final Jackson JACKSON = Jackson.DEFAULT;

  public static String pretty(Object object) {
    return JACKSON.pretty(object);
  }

  public static String to(Object object) {
    return JACKSON.toString(object, false);
  }

  public static <T> T from(String json, Class<T> clazz) {
    return JACKSON.from(json, clazz);
  }

	public static <T> T from(String json, TypeReference<T> typeReference) {
    return JACKSON.from(json, typeReference);
  }

}
