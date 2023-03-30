package cachemesh.common;

public interface Serderializer extends HasName {

	byte[] serialize(Object obj);

	<T> T deserialize(byte[] bytes, Class<T> clazz);

}
