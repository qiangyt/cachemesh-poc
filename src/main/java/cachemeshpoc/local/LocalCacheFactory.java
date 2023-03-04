package cachemeshpoc.local;

public interface LocalCacheFactory {

	<T> LocalCache<T> create(String cacheName, Class<T> valueClass);

}
