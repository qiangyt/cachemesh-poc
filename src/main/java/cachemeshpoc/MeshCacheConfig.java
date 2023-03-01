package cachemeshpoc;

import java.util.List;

@lombok.Data
@lombok.Builder
public class MeshCacheConfig<V> {

	private String name;

	private Class<V> valueClass;

	private Serderializer serder;

}
