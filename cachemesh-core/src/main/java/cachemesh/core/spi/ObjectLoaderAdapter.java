package cachemesh.core.spi;

import java.util.Objects;
import java.util.function.Function;

import cachemesh.core.bean.Value;
import lombok.Getter;

@Getter
public class ObjectLoaderAdapter implements ObjectLoader {
    
    private final Class<?> valueClass;

    private final Function<String, Value<Object>> loaderFunc;

    public ObjectLoaderAdapter(Class<?> valueClass, Function<String, Value<Object>> loaderFunc) {
        this.valueClass = valueClass;
        this.loaderFunc = loaderFunc;
    }    

    public <V> Function<V, Value<Object>> compose(Function<? super V, ? extends String> before) {
        Objects.requireNonNull(before);
        return v -> apply(before.apply(v));
    }

    public <V> Function<String, V> andThen(Function<? super Value<Object>, ? extends V> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

    @Override
    public Value<Object> apply(String key) {
        return getLoaderFunc().apply(key);
    }

    public static ObjectLoaderAdapter of(Class<?> valueClass, Function<String, Value<Object>> loaderFunc) {
        return new ObjectLoaderAdapter(valueClass, loaderFunc);
    }

}
