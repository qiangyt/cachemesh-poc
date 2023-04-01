package cachemesh.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import cachemesh.spi.Transport;


public class TransportRegistry {

	private final Map<String, Transport> transports = new ConcurrentHashMap<>();

	public void register(Transport transport) {
		this.transports.compute(transport.getProtocol(), (protocol, existing) -> {
			if (existing != null) {
				throw new IllegalArgumentException("duplicated transport transport: " + protocol);
			}
			return transport;
		});
	}

	public Transport get(String protocol) {
		return this.transports.get(protocol);
	}

}
