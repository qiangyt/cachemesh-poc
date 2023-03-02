package cachemeshpoc;

public interface MeshCache<V> extends AutoCloseable {

	@lombok.Data
	@lombok.Builder
	public class Config<V> {

		private String name;

		private Class<V> valueClass;

		private Serderializer serder;

	}

	Config<V> getConfig();

	V getSingle(String key);

	void setSingle(String key, V value);

}
