package cachemesh.spi;

import java.util.Map;

import cachemesh.common.HasName;
import cachemesh.core.LocalNodeCache;

public interface Transport extends HasName {

	default String getProtocol() {
		return getName();
	}

	Map<String,Object> parseUrl(String url);

	default boolean setUpForLocalNode(Map<String,Object> configMap, LocalNodeCache localNodeCache) {
		return false;
	}

	NodeCache setUpForRemoteNode(Map<String,Object> configMap);

}
