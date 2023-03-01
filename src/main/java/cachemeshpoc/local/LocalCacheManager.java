package cachemeshpoc.local;

public interface LocalCacheManager {

	void register(LocalCache cache);

	LocalCache get(String cacheName);

	LocalCache resolve(String cacheName);

}
