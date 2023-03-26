package cachemesh.spi.base;

import java.lang.ref.SoftReference;

public class CombinedValue<T> extends Value<T> {

	private SoftReference<byte[]> bytesReferent;

	public CombinedValue(T data, long version) {
		super(data, version);
	}

	public void setBytes(byte[] bytes) {
		this.bytesReferent = new SoftReference<byte[]>(bytes);
	}

	public byte[] getBytes() {
		var ref = this.bytesReferent;
		if (ref == null) {
			return null;
		}
		return ref.get();
	}

}
