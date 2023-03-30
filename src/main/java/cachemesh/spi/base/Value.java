package cachemesh.spi.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import cachemesh.common.Serderializer;

public interface Value {

	static final Object NULL_OBJECT = new Object();

	static final byte[] NULL_BYTES = new byte[]{};

	static final Reference<byte[]> NULL_BYTES_R = new WeakReference<>(NULL_BYTES);

	static final Reference<Object> NULL_OBJECT_R = new WeakReference<>(NULL_OBJECT);

	boolean hasValue();

	boolean isNullValue();

	<T> T getObject(Serderializer serder, Class<?> valueClass);

	<T> T getObject();

	byte[] getBytes(Serderializer serder);

	byte[] getBytes();

	long getVersion();

}
