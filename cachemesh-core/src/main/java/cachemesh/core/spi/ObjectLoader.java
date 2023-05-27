package cachemesh.core.spi;

import java.util.function.Function;

import cachemesh.core.bean.Value;

public interface ObjectLoader extends Function<String, Value<Object>> {

    Class<?> getValueClass();

    public static ObjectLoader of(Class<?> valueClass, Function<String, Value<Object>> loaderFunc) {
        return ObjectLoaderAdapter.of(valueClass, loaderFunc);
    }

}
