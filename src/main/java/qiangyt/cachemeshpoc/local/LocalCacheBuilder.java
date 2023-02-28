package qiangyt.cachemeshpoc.local;

public interface LocalCacheBuilder {

	<V> LocalCache<V> build(String name, Class<V> valueClass);

}
