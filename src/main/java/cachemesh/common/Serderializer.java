package cachemesh.common;

public interface Serderializer extends HasName {

	byte[] serialize(Object obj);

	Object deserialize(byte[] bytes, Class<?> clazz);

}
