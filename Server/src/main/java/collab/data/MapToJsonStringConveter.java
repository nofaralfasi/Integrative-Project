package collab.data;

import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MapToJsonStringConveter implements AttributeConverter<Map<String, Object>, String> {
	private ObjectMapper jackson;

	public MapToJsonStringConveter() {
		this.jackson = new ObjectMapper();
	}

	@Override
	public String convertToDatabaseColumn(Map<String, Object> attribute) {
		try {
			return this.jackson.writeValueAsString(attribute);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		try {
			return this.jackson.readValue(dbData, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
