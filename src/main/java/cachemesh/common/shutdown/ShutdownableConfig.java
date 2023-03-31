package cachemesh.common.shutdown;

import cachemesh.common.HasName;

public interface ShutdownableConfig extends HasName {

	int getShutdownTimeoutSeconds();

}
