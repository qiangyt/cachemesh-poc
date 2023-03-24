package cachemesh.common.convert;

import java.nio.ByteBuffer;
import com.google.protobuf.ByteString;


public class ByteStringConverters {

	static {
		register(ConverterRegistry.DEFAULT);
	}

	public static void register(ConverterRegistry registry) {
		registry.register(ByteString.class, ByteBuffer.class, ByteStringConverters::forByteBuffer);
		registry.register(ByteString.class, byte[].class, ByteStringConverters::forByteArray);
		registry.register(ByteString.class, ByteString.class, ByteStringConverters::forByteString);
	}

	public static ByteBuffer forByteBuffer(ByteString input) {
		return ByteBuffer.wrap(input.toByteArray());
	}

	public static byte[] forByteArray(ByteString input) {
		return input.toByteArray();
	}

	public static ByteString forByteString(ByteString input) {
		return input;
	}

}
