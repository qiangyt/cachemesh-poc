package cachemesh.core.spi;

import java.util.function.BiFunction;

import cachemesh.core.bean.Value;

public interface ValueMapper extends BiFunction<String, Value<Object>, Value<Object>> {
    
    Class<?> getValueClass();

}
