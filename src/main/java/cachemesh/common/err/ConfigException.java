package cachemesh.common.err;

public class ConfigException extends InternalException {

	public ConfigException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

	public ConfigException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
