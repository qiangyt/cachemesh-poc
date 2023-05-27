package cachemesh.core.spi;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import cachemesh.core.bean.Value;
import lombok.Getter;

@Getter
public class ObjectMapperAdapter implements ObjectMapper {
    
    private final Class<?> valueClass;

    private final BiFunction<String, Value<Object>, Value<Object>> mapperFunc;

    public ObjectMapperAdapter(Class<?> valueClass, BiFunction<String, Value<Object>, Value<Object>> mapperFunc) {
        this.valueClass = valueClass;
        this.mapperFunc = mapperFunc;
    }

    public <V> BiFunction<String, Value<Object>, V> andThen(Function<? super Value<Object>, ? extends V> after) {
        Objects.requireNonNull(after);
        return (t, u) -> after.apply(apply(t, u));
    }

    @Override
    public Value<Object> apply(String key, Value<Object> oldValue) {
        return getMapperFunc().apply(key, oldValue);
    }

    public static ObjectMapperAdapter of(Class<?> valueClass, BiFunction<String, Value<Object>, Value<Object>> mapperFunc) {
        return new ObjectMapperAdapter(valueClass, mapperFunc);
    }

}
