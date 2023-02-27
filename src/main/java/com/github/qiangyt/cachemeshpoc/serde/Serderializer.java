package com.github.qiangyt.cachemeshpoc.serde;

public interface Serderializer {

	byte[] serialize(Object obj);

	<T> T deserialize(byte[] bytes, Class<T> clazz);

}
