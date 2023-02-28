package qiangyt.cachemeshpoc.err;

public class CacheMeshInternalException extends CacheMeshException {

	public CacheMeshInternalException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public CacheMeshInternalException(Throwable cause) {
		super(cause);
	}

	public CacheMeshInternalException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
