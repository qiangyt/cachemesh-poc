package cachemesh.spi.base;

import java.lang.ref.WeakReference;

public class CombinedValue<T> extends Value<T> {

	private WeakReference<byte[]> bytesReferent; //TODO: or use SoftReference; have this configurable

	public CombinedValue(T data, long version) {
		super(data, version);
	}

	public void setBytes(byte[] bytes) {
		this.bytesReferent = new WeakReference<>(bytes);
	}

	public byte[] getBytes() {
		var ref = this.bytesReferent;
		if (ref == null) {
			return null;
		}
		return ref.get();
	}

}
