package qiangyt.cachemeshpoc.local;

public interface LocalCache<V> extends AutoCloseable {

	String getName();

	Class<V> getValueClass();

	LocalEntry<V> getSingle(String key);

	void setSingle(LocalEntry<V> entry);

}
