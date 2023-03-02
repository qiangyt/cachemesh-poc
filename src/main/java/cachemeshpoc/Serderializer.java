package cachemeshpoc;

public interface Serderializer {

	byte[] serialize(Object obj);

	<T> T deserialize(byte[] bytes, Class<?> clazz);

}
