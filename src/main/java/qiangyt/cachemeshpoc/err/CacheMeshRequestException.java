package qiangyt.cachemeshpoc.err;

public class CacheMeshRequestException extends CacheMeshException {

	public CacheMeshRequestException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

	public CacheMeshRequestException(Throwable cause) {
		super(cause);
	}

	public CacheMeshRequestException(Throwable cause, String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

}
