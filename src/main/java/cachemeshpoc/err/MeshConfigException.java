package cachemeshpoc.err;

public class MeshConfigException extends MeshInternalException {

	public MeshConfigException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public MeshConfigException(Throwable cause) {
		super(cause);
	}

	public MeshConfigException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
