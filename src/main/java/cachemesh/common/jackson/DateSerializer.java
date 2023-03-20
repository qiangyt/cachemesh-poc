package cachemesh.common.jackson;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonProcessingException;

public class DateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeNumber(value.getTime());
		}
	}
}
