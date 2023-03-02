package cachemeshpoc;

public interface RawCache extends AutoCloseable {

	public static interface Builder {
		RawCache build();
	}

	MeshResult resolveSingle(String cacheName, String key, long version);

	// return version
	long putSingle(String cacheName, String key, byte[] bytes);

}
