package cachemesh.lettuce;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import io.lettuce.core.codec.RedisCodec;
import lombok.Getter;

@Getter
public class LettuceCodec implements RedisCodec<String, byte[]> {

	public static final LettuceCodec DEFAULT = new LettuceCodec(StandardCharsets.UTF_8);

	private final Charset charset;

	public LettuceCodec(Charset charset) {
		this.charset = charset;
	}

	@Override
	public String decodeKey(ByteBuffer bytes) {
		if (!bytes.hasArray()) {
			bytes = bytes.get(new byte[bytes.remaining()]);
		}

		final int offset = bytes.arrayOffset();
		return new String(bytes.array(), offset + bytes.position(), bytes.remaining(), getCharset());
	}

	@Override
	public byte[] decodeValue(ByteBuffer bytes) {
		byte[] r = new byte[bytes.remaining()];
		bytes.get(r);
		return r;
	}

	@Override
	public ByteBuffer encodeKey(String key) {
		return ByteBuffer.wrap(key.getBytes(getCharset()));
	}

	@Override
	public ByteBuffer encodeValue(byte[] value) {
		return ByteBuffer.wrap(value);
	}

}
