package cachemesh.common.err;

public class ServiceException extends InternalException {

	public ServiceException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
