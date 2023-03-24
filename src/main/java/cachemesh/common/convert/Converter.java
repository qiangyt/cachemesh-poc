package cachemesh.common.convert;

@FunctionalInterface
public interface Converter<S,T> {

	T convert(S src);

}
