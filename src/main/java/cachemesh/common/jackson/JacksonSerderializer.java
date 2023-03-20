package cachemesh.common.jackson;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cachemesh.common.Serderializer;

@lombok.Getter
public class JacksonSerderializer implements Serderializer {

	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public static final JacksonSerderializer DEFAULT = new JacksonSerderializer();

	private final JacksonHelper jackson;

	private final Charset charset;

	public JacksonSerderializer() {
		this(JacksonHelper.DEFAULT, DEFAULT_CHARSET);
	}

	public JacksonSerderializer(JacksonHelper jackson, Charset charset) {
		this.jackson = jackson;
		this.charset = charset;
	}

	@Override
	public String getName() {
		return "jackson";
	}

	@Override
	public byte[] serialize(Object obj) {
		String s = this.jackson.to(obj);
		return s.getBytes(StandardCharsets.UTF_8);
	}

	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
		String s = new String(bytes, StandardCharsets.UTF_8);
		return (T)this.jackson.from(s, clazz);
	}

}
