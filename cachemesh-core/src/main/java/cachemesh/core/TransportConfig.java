package cachemesh.core;

import java.util.HashMap;
import java.util.Map;

import cachemesh.common.Mappable;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class TransportConfig implements Mappable {

	private final int startTimeoutSeconds;

	private final int stopTimeoutSeconds;

	public abstract String getProtocol();

	public abstract boolean isRemote();

	@Override
	public Map<String, Object> toMap() {
		var configMap = new HashMap<String, Object>();

		configMap.put("startTimeoutSeconds", getStartTimeoutSeconds());
		configMap.put("stopTimeoutSeconds", getStopTimeoutSeconds());
		configMap.put("remote", isRemote());
		configMap.put("protocol", getProtocol());
		configMap.put("target", getTarget());

		return configMap;
	}

	public abstract String getTarget();

}
