package cachemesh.core;

import cachemesh.common.Registry;
import cachemesh.spi.TransportProvider;


public class TransportRegistry extends Registry<TransportConfig, TransportProvider> {

	public static final TransportRegistry DEFAULT = new TransportRegistry();

	@Override
	protected String retrieveKey(TransportConfig transport) {
		return transport.getProtocol();
	}

}
