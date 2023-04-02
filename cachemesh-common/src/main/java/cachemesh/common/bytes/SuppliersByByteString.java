package cachemesh.common.bytes;

import java.nio.ByteBuffer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.protobuf.ByteString;

import cachemesh.common.err.InternalException;

public class SuppliersByByteString {

	public static Function<ByteString, Supplier<?>> toFactory(Class<?> targetType) {
		if (targetType == ByteBuffer.class) {
			return input -> toByteBuffer(input);
		}
		if (targetType == byte[].class) {
			return input -> toByteArray(input);
		}
		if (targetType == ByteString.class) {
			return input -> toByteString(input);
		}

		throw new InternalException("%s->%s is not supported", ByteString.class, targetType);
	}

	public static Supplier<ByteBuffer> toByteBuffer(ByteString input) {
		return () -> ByteBuffer.wrap(input.toByteArray());
	}

	public static Supplier<byte[]> toByteArray(ByteString input) {
		return input::toByteArray;
	}

	public static Supplier<ByteString> toByteString(ByteString input) {
		return () -> input;
	}


	public static Function<Object, Supplier<ByteString>> fromFactory(Class<?> srcType) {
		if (srcType == ByteBuffer.class) {
			return input -> fromByteBuffer((ByteBuffer)input);
		}
		if (srcType == byte[].class) {
			return input -> fromByteArray((byte[])input);
		}
		if (srcType == ByteString.class) {
			return input -> fromByteString((ByteString)input);
		}

		throw new InternalException("%s->%s is not supported", srcType, ByteString.class);
	}

	public static Supplier<ByteString> fromByteBuffer(ByteBuffer input) {
		return () -> ByteString.copyFrom(input);
	}

	public static Supplier<ByteString> fromByteArray(byte[] input) {
		return () -> ByteString.copyFrom(input);
	}

	public static Supplier<ByteString> fromByteString(ByteString input) {
		return () -> input;
	}

}
