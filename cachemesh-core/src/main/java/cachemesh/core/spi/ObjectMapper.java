package cachemesh.core.spi;

import java.util.function.BiFunction;

import cachemesh.core.bean.Value;

public interface ObjectMapper extends BiFunction<String, Value<Object>, Value<Object>> {
    
    Class<?> getValueClass();

    public static ObjectMapper of(Class<?> valueClass, BiFunction<String, Value<Object>, Value<Object>> mapperFunc) {
        return ObjectMapperAdapter.of(valueClass, mapperFunc);
    }

}
