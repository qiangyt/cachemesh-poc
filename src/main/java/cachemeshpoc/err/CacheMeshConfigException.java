package cachemeshpoc.err;

public class CacheMeshConfigException extends CacheMeshInternalException {

	public CacheMeshConfigException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public CacheMeshConfigException(Throwable cause) {
		super(cause);
	}

	public CacheMeshConfigException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
