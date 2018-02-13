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
public enum EnumDatasetAction {

    /**
     * Undefined value of the dataset action.
     */
    UNDEFINED(0),

    /**
     * Keeps the model of the entity from the left source dataset.
     */
    KEEP_LEFT(1),

    /**
     * Keeps the model of the entity from the right source dataset.
     */
    KEEP_RIGHT(2),

    /**
     * Keeps both models of the entity from left and right source datasets.
     */
    KEEP_BOTH(3),

    /**
     * Rejects the link, that is the fusion action. 
     * If a new fused dataset is chosen as output, none of the respective models of the entities appear in the final dataset.
     */    
    REJECT_LINK(4);

	private final int value;
    
	private static final Map<Integer, EnumDatasetAction> intToTypeMap = new HashMap<>();
	static {
		for (EnumDatasetAction type : EnumDatasetAction.values()) {
			intToTypeMap.put(type.value, type);
		}
	}
         
	private EnumDatasetAction(int value) {
		this.value = value;
	}

    /**
     * Returns the integer value of the action.
     * @return
     */
    public int getValue() {
		return this.value;
	}

    /**
     * Returns the EnumDatasetAction object from its integer value or UNDEFINED if the type does not exist.
     * 
     * @param value the integer value of the action.
     * @return the type of the action.
     */
    public static EnumDatasetAction fromInteger(int value) {
		EnumDatasetAction type = intToTypeMap.get(value);
		if (type == null)
			return EnumDatasetAction.UNDEFINED;
		return type;
	}

    /**
     * Returns the EnumDatasetAction object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value
     * @return
     */
    public static EnumDatasetAction fromString(String value) {
		for (EnumDatasetAction item : EnumDatasetAction.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumDatasetAction.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumDatasetAction> {
        
        /**
         * Deserializes the EnumDatasetAction
         * 
         * @param parser the Json parser
         * @param context the deserialization context
         * @return the EnumDatasetAction
         * @throws java.io.IOException
         * @throws com.fasterxml.jackson.core.JsonProcessingException
         */
		@Override
		public EnumDatasetAction deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumDatasetAction.fromString(parser.getValueAsString());
		}
	}
    
    @Override
    public String toString() {
      switch(this) {
        case UNDEFINED: return "undefined";
        case KEEP_LEFT: return "keep-left";
        case KEEP_RIGHT: return "keep-right";
        case KEEP_BOTH: return "keep-both";
        case REJECT_LINK: return "reject-link";
        default: throw new IllegalArgumentException();
      }
    }    
}
