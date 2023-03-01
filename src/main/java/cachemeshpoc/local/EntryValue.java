package cachemeshpoc.local;

import cachemeshpoc.Serderializer;

@lombok.Builder
public class EntryValue {

	@lombok.Getter
	@lombok.Setter
	private long version;

	private Object object;

	private byte[] bytes;

	public <V> V getObject(Serderializer serder, Class<V> valueClass) {
		if (this.object == null) {
			if (this.bytes == null) {
				return null;
			}
			this.object = serder.deserialize(this.bytes, valueClass);
		}

		@SuppressWarnings("unchecked")
		var r = (V)this.object;
		return r;
	}

	public byte[] getBytes(Serderializer serder) {
		if (this.bytes == null) {
			if (this.object == null) {
				return null;
			}
			this.bytes = serder.serialize(this.object);
		}
		return this.bytes;
	}

}
