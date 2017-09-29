package gr.athena.innovation.fagi.core.action;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for default dataset actions.
 * 
 * @author nkarag
 */
public enum EnumDatasetActions {
	UNDEFINED(0), KEEP_LEFT(1), KEEP_RIGHT(2), KEEP_BOTH(3);

	private final int value;
    
	private static final Map<Integer, EnumDatasetActions> intToTypeMap = new HashMap<>();
	static {
		for (EnumDatasetActions type : EnumDatasetActions.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumDatasetActions(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static EnumDatasetActions fromInteger(int value) {
		EnumDatasetActions type = intToTypeMap.get(value);
		if (type == null)
			return EnumDatasetActions.UNDEFINED;
		return type;
	}

	public static EnumDatasetActions fromString(String value) {
		for (EnumDatasetActions item : EnumDatasetActions.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumDatasetActions.UNDEFINED;
	}

	public static class Deserializer extends JsonDeserializer<EnumDatasetActions> {

		@Override
		public EnumDatasetActions deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumDatasetActions.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case KEEP_LEFT: return "Keep Left";
        case KEEP_RIGHT: return "Keep Right";
        case KEEP_BOTH: return "Keep Both";
        default: throw new IllegalArgumentException();
      }
    }    
}