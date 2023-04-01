package cachemesh.spi;

import java.util.Map;

import cachemesh.core.LocalTransport;
import cachemesh.core.TransportConfig;

public interface TransportProvider extends NodeHook {

	String getProtocol();

	Map<String,Object> parseUrl(String url);

	TransportConfig parseConfig(Map<String,Object> configMap);

	default boolean setUpLocalTransport(TransportConfig transportConfig, LocalTransport localTranport) {
		return false;
	}

	Transport createRemoteTransport(TransportConfig transportConfig);

}
