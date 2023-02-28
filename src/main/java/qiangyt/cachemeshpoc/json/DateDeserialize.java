package qiangyt.cachemeshpoc.json;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.databind.JsonDeserializer;
import qiangyt.cachemeshpoc.err.CacheMeshRequestException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public class DateDeserialize extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		String valueText = p.getValueAsString();

		try {
			return new Date(Long.valueOf(valueText));
		} catch (NumberFormatException ex) {
			throw new CacheMeshRequestException(ex, "%s is NOT a long value", valueText);
		}
	}
}
