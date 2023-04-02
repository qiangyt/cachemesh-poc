package cachemesh.common.jackson;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cachemesh.common.Serderializer;
import lombok.Getter;

@Getter
public class JacksonSerderializer implements Serderializer {

	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public static final JacksonSerderializer DEFAULT = new JacksonSerderializer();

	private final Jackson jackson;

	private final Charset charset;

	public JacksonSerderializer() {
		this(Jackson.DEFAULT, DEFAULT_CHARSET);
	}

	public JacksonSerderializer(Jackson jackson, Charset charset) {
		this.jackson = jackson;
		this.charset = charset;
	}

	@Override
	public String getName() {
		return "jackson";
	}

	@Override
	public byte[] serialize(Object obj) {
		return this.jackson.toBytes(obj);
	}

	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
		return this.jackson.from(bytes, clazz);
	}

}
