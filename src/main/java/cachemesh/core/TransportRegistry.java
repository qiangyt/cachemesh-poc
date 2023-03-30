package cachemesh.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import cachemesh.spi.TransportProvider;


public class TransportRegistry {

	private final Map<String, TransportProvider> providers = new ConcurrentHashMap<>();

	public void register(TransportProvider provider) {
		this.providers.compute(provider.getProtocol(), (protocol, existing) -> {
			if (existing != null) {
				throw new IllegalArgumentException("duplicated transport provider: " + protocol);
			}
			return provider;
		});
	}

	public TransportProvider get(String protocol) {
		return this.providers.get(protocol);
	}

}
