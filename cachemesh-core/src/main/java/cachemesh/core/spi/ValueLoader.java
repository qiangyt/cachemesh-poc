package cachemesh.core.spi;

import java.util.function.Function;

public interface ValueLoader extends Function<String, Object> {
    
    Class<?> getValueClass();

}
