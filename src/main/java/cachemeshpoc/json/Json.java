package cachemeshpoc.json;

import com.fasterxml.jackson.core.type.TypeReference;

public class Json {

  public static final JacksonHelper jackson = new JacksonHelper(JacksonHelper.buildMapper());

  public static String pretty(Object object) {
    return to(object, true);
  }

  public static String to(Object object) {
    return to(object, false);
  }

  public static String to(Object object, boolean pretty) {
    return jackson.to(object, pretty);
  }

  public static <T> T from(String json, Class<T> clazz) {
    return jackson.from(json, clazz);
  }

	public static <T> T from(String json, TypeReference<T> typeReference) {
    return jackson.from(json, typeReference);
  }

}
