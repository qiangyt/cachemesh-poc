package cachemeshpoc;

public interface NodeCache {

	String getName();

	GetResult<byte[]> getSingle(String key, long versh);

	// return versh
	long putSingle(String key, byte[] bytes);


}
