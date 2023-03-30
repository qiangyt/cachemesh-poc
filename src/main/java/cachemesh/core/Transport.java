package cachemesh.core;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;

@lombok.Getter
public class Transport implements Mappable {

	public static final String URL_SEPARATOR = "://";

	private final String protocol;

	private final String target;


	public Transport(String protocol, String target) {
		this.protocol = protocol;
		this.target = target;
	}


	public String formatUrl() {
		return formatUrl(getProtocol(), getTarget());
	}


	public void ensureProtocol(String expectedProtocol) {
		if (getProtocol().equals(expectedProtocol) == false) {
			throw new IllegalArgumentException("protocol must be " + expectedProtocol);
		}
	}

	public static String formatUrl(String protocol, String target) {
		return protocol + URL_SEPARATOR + target;
	}


	public static Transport parseUrl(String url) {
		int sep = url.indexOf(URL_SEPARATOR);
		if (sep <= 0) {
			throw new IllegalArgumentException("url should follow the format: <url>" + URL_SEPARATOR + "<target>");
		}

		String protocol = url.substring(0, sep);
		String target = url.substring(sep + URL_SEPARATOR.length());

		return new Transport(protocol, target);
	}


	@Override
	public Map<String, Object> toMap() {
		var r = new HashMap<String, Object>();

		r.put("protocol", getProtocol());
		r.put("target", getTarget());

		return r;
	}

	public static String getTarget(Map<String, Object> configMap) {
		return "target";
	}

}
