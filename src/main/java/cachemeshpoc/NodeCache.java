package cachemeshpoc;

public interface NodeCache {

	String getName();

	GetResult<byte[]> getSingle(String key, long version);

	// return version
	long putSingle(String key, byte[] bytes);


}
