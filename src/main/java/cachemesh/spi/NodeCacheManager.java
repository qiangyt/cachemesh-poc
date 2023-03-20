package cachemesh.spi;

public interface NodeCacheManager extends AutoCloseable {
	NodeCache get(String cacheName);

	NodeCache resolve(String cacheName);
}
