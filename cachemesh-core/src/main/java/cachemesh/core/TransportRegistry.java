package cachemesh.core;

import cachemesh.common.Registry;
import cachemesh.core.spi.TransportProvider;


public class TransportRegistry extends Registry<String, TransportProvider> {

	public static final TransportRegistry DEFAULT = new TransportRegistry();

	@Override
	protected String retrieveKey(String protocol) {
		return protocol;
	}

}
