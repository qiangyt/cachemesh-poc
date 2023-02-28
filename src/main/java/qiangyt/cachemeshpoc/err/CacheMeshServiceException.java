package qiangyt.cachemeshpoc.err;

public class CacheMeshServiceException extends CacheMeshInternalException {

	public CacheMeshServiceException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public CacheMeshServiceException(Throwable cause) {
		super(cause);
	}

	public CacheMeshServiceException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
