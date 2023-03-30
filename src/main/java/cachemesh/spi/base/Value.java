package cachemesh.spi.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import cachemesh.common.Serderializer;
import lombok.Getter;

public class Value {

	private Reference<byte[]> bytesR;

	@Getter
	private Object object;

	@Getter
	private long version;

	public Value(byte[] bytes, long version) {
		this.bytesR = new WeakReference<>(bytes);
		this.version = version;
	}

	public Value(Object object, long version) {
		this.object = object;
		this.version = version;
	}

	public Object getObject(Serderializer serder, Class<?> valueClass) {
		if (this.object == null) {
			byte[] bytes = (this.bytesR == null) ? null : this.bytesR.get();
			if (bytes == null) {
				return null;
			}
			this.object = serder.deserialize(bytes, valueClass);
		}
		return this.object;
	}

	public byte[] getBytes(Serderializer serder) {
		byte[] bytes = (this.bytesR == null) ? null : this.bytesR.get();
		if (bytes != null) {
			return bytes;
		}

		if (this.object == null) {
			return null;
		}
		bytes = serder.serialize(this.object);
		this.bytesR = new WeakReference<>(bytes);//TODO: or use SoftReference; have this configurable

		return bytes;
	}




}
