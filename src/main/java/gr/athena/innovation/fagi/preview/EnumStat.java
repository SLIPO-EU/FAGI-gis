package gr.athena.innovation.fagi.preview;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public enum EnumStat {

    /**
     * Undefined value of the statistic.
     */
    UNDEFINED("undefined"),

    /**
     * Number of POI entities in each input dataset.
     */
    TOTAL_POIS("totalPois"),
    
    /**
     * Total number of triples in each input dataset.
     */    
    TOTAL_TRIPLES("totalTriples"),
    
    /**
     * Number of non empty names in each input dataset.
     */    
    NON_EMPTY_NAMES("nonEmptyNames"),

    /**
     * Number of non empty phone numbers in each input dataset.
     */    
    NON_EMPTY_PHONES("nonEmptyPhones"),

    /**
     * Number of non empty street addresses in each input dataset.
     */    
    NON_EMPTY_STREETS("nonEmptyStreets"),

    /**
     * Number of non empty address street numbers in each input dataset.
     */    
    NON_EMPTY_STREET_NUMBERS("nonEmptyStreetNumbers"),

    /**
     * Number of non empty web-sites in each input dataset.
     */    
    NON_EMPTY_WEBSITES("nonEmptyWebsites"),
    
    /**
     * Number of non empty e-mails in each input dataset.
     */    
    NON_EMPTY_EMAILS("nonEmptyEmails"),
    
    /**
     * Number of non empty dates in each input dataset.
     */    
    NON_EMPTY_DATES("nonEmptyDates"),
    /**
     * Number of empty names in each input dataset.
     */    
    EMPTY_NAMES("emptyNames"),

    /**
     * Number of empty phone numbers in each input dataset.
     */    
    EMPTY_PHONES("emptyPhones"),

    /**
     * Number of empty street addresses in each input dataset.
     */    
    EMPTY_STREETS("emptyStreets"),

    /**
     * Number of empty address street numbers in each input dataset.
     */    
    EMPTY_STREET_NUMBERS("emptyStreetNumbers"),

    /**
     * Number of empty web-sites in each input dataset.
     */    
    EMPTY_WEBSITES("emptyWebsites"),
    
    /**
     * Number of empty e-mails in each input dataset.
     */    
    EMPTY_EMAILS("emptyEmails"),
    
    /**
     * Number of empty dates in each input dataset.
     */    
    EMPTY_DATES("emptyDates"),
    
    /**
     * Number of distinct properties in each input dataset.
     */    
    DISTINCT_PROPERTIES("distinctProperties"),
    
    /**
     * Percentage of dates that follow the primary format as described in the spec.
     */    
    PRIMARY_DATE_FORMATS_PERCENT("primaryDatesFormatPercent"),
    
    /**
     * Percentage of name property in each input dataset.
     */    
    NAMES_PERCENT("namesPercent"),  
    
    /**
     * Percentage of web-site property in each input dataset.
     */    
    WEBSITE_PERCENT("websitesPercent"),
    
    /**
     * Percentage of e-mail property in each input dataset.
     */    
    EMAIL_PERCENT("emailsPercent"),      

    /**
     * Percentage of phone property in each input dataset.
     */    
    PHONES_PERCENT("namesPercent"),  

    /**
     * Percentage of street property in each input dataset.
     */    
    STREETS_PERCENT("streetsPercent"),  

    /**
     * Percentage of street number property in each input dataset.
     */    
    STREET_NUMBERS_PERCENT("streetNumbersPercent"),  

    /**
     * Percentage of locality property in each input dataset.
     */    
    LOCALITY_PERCENT("localityPercent"),

    /**
     * Percentage of dates property in each input dataset.
     */    
    DATES_PERCENT("datesPercent"),

    /**
     * Number of linked POIs in each input dataset.
     */    
    LINKED_POIS("linkedPois"),

    /**
     * Number of linked vs total POIS in the datasets.
     */
    LINKED_VS_TOTAL("linkedVsTotal"),

    /**
     * Number of linked triples from input datasets.
     */
    LINKED_TRIPLES("linkedTriples"),

    /**
     * Number of non empty names of linked entities.
     */
    LINKED_NON_EMPTY_NAMES("linkedNonEmptyNames");

	private final String value;

	private static final Map<String, EnumStat> stringToTypeMap = new HashMap<>();

	static {
		for (EnumStat type : EnumStat.values()) {
			stringToTypeMap.put(type.value, type);
		}
	}

	private EnumStat(String value) {
		this.value = value;
	}

    /**
     * Returns the string value (key) of the statistic.
     * @return The value.
     */
    public String getKey() {
		return this.value;
	}

    /**
     * Returns the mapping of keys to enumStat type.
     * @return The map.
     */
    public static Map<String, EnumStat> getMap() {
		return EnumStat.stringToTypeMap;
	}
    
    /**
     * Returns the EnumStat object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value The statistic value.
     * @return The statistic.
     */
    public static EnumStat fromString(String value) {
		for (EnumStat item : EnumStat.values()) {
			if (item.toString().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return EnumStat.UNDEFINED;
	}

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumStat> {

        /**
         * Deserializes the EnumStat
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the EnumStat
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
		@Override
		public EnumStat deserialize(JsonParser parser, DeserializationContext context) throws IOException,
						JsonProcessingException {
			return EnumStat.fromString(parser.getValueAsString());
		}
	}

    @Override
    public String toString() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Number of POI entities in each input dataset.";
            case TOTAL_TRIPLES: return "Total number of triples in each input dataset.";
            case NON_EMPTY_NAMES: return "Total number of POIs that have the name property in each input dataset.";
            case NON_EMPTY_PHONES: return "Total number of POIs that have the phone property in each input dataset.";
            case NON_EMPTY_STREETS: return "Total number of POIs that have the address street property in each input dataset.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in each input dataset.";
            case NON_EMPTY_WEBSITES: return "Total number of POIs that have the website property in each input dataset.";
            case NON_EMPTY_EMAILS: return "Total number of POIs that have the email property in each input dataset.";
            case NON_EMPTY_DATES: return "Total number of POIs that have the date property in each input dataset.";
            case EMPTY_NAMES: return "Total number of POIs that have the name property in each input dataset.";
            case EMPTY_PHONES: return "Total number of POIs that have the phone property in each input dataset.";
            case EMPTY_STREETS: return "Total number of POIs that have the address street property in each input dataset.";
            case EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in each input dataset.";
            case EMPTY_WEBSITES: return "Total number of POIs that have the website property in each input dataset.";
            case EMPTY_EMAILS: return "Total number of POIs that have the email property in each input dataset.";
            case EMPTY_DATES: return "Total number of POIs that have the date property in each input dataset.";
            case DISTINCT_PROPERTIES: return "Number of distinct properties in each input dataset.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Percentage of primary date formats in each input dataset.";
            case NAMES_PERCENT: return "Percentage of name property in each input dataset.";
            case WEBSITE_PERCENT: return "Percentage of website property in each input dataset.";
            case EMAIL_PERCENT: return "Percentage of email property in each input dataset.";
            case PHONES_PERCENT: return "Percentage of phone property in each input dataset.";
            case STREETS_PERCENT: return "Percentage of street property in each input dataset.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street number property in each input dataset.";
            case LOCALITY_PERCENT: return "Percentage of locality property in each input dataset.";
            case DATES_PERCENT: return "Percentage of locality property in each input dataset.";
            case LINKED_POIS: return "Number of linked POIs in each input dataset.";
            case LINKED_VS_TOTAL: return "Number of linked vs total POIs in the datasets.";
            case LINKED_TRIPLES: return "Number of linked triples from each dataset.";
            case LINKED_NON_EMPTY_NAMES: return "Number of linked POIs that have the name property in each input dataset.";
            default: throw new IllegalArgumentException();
        }
    }

    public String getLegendTotal() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Total POIs.";
            case TOTAL_TRIPLES: return "Total Triples.";
            case NON_EMPTY_NAMES: return "Total number of name properties.";
            case NON_EMPTY_PHONES: return "Total number of phone properties.";
            case NON_EMPTY_STREETS: return "Total number of address street property.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of address street number property.";
            case NON_EMPTY_WEBSITES: return "Total number of website property.";
            case NON_EMPTY_EMAILS: return "Total number of email property.";
            case NON_EMPTY_DATES: return "Total number of date property.";
            case EMPTY_NAMES: return "Total number of empty name properties.";
            case EMPTY_PHONES: return "Total number of empty phone properties.";
            case EMPTY_STREETS: return "Total number of empty address street property.";
            case EMPTY_STREET_NUMBERS: return "Total number empty of address street number property.";
            case EMPTY_WEBSITES: return "Total number of empty website property.";
            case EMPTY_EMAILS: return "Total number of empty email property.";
            case EMPTY_DATES: return "Total number of empty date property.";
            case DISTINCT_PROPERTIES: return "Distinct Properties.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Primary date formats.";
            case NAMES_PERCENT: return "Percentage of names.";
            case WEBSITE_PERCENT: return "Percentage of websites.";
            case EMAIL_PERCENT: return "Percentage of e-mails.";
            case PHONES_PERCENT: return "Percentage of phones.";
            case STREETS_PERCENT: return "Percentage of streets.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street numbers.";
            case LOCALITY_PERCENT: return "Percentage of locality.";
            case DATES_PERCENT: return "Percentage of dates.";
            case LINKED_POIS: return "Linked POIs.";
            case LINKED_VS_TOTAL: return "Linked vs total POIs.";
            case LINKED_TRIPLES: return "Linked Triples.";
            case LINKED_NON_EMPTY_NAMES: return "Linked non empty names.";
            default: throw new IllegalArgumentException();
        }
    }
    
    public String getLegendA() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Number of POIs in A.";
            case TOTAL_TRIPLES: return "Number of triples in A.";
            case NON_EMPTY_NAMES: return "Total number of POIs that have the name property in dataset A.";
            case NON_EMPTY_PHONES: return "Total number of POIs that have the phone property in dataset A.";
            case NON_EMPTY_STREETS: return "Total number of POIs that have the address street property in dataset A.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in dataset A.";
            case NON_EMPTY_WEBSITES: return "Total number of POIs that have the website property in dataset A.";
            case NON_EMPTY_EMAILS: return "Total number of POIs that have the email property in dataset A.";
            case NON_EMPTY_DATES: return "Total number of POIs that have the date property in dataset A.";
            case EMPTY_NAMES: return "Total number of POIs that don' t have the name property in dataset A.";
            case EMPTY_PHONES: return "Total number of POIs that don' t have the phone property in dataset A.";
            case EMPTY_STREETS: return "Total number of POIs that don' t have the address street property in dataset A.";
            case EMPTY_STREET_NUMBERS: return "Total number of POIs that don' t have the address street number property in dataset A.";
            case EMPTY_WEBSITES: return "Total number of POIs that don' t have the website property in dataset A.";
            case EMPTY_EMAILS: return "Total number of POIs that don' t have the email property in dataset A.";
            case EMPTY_DATES: return "Total number of POIs that don' t have the date property in dataset A.";
            case DISTINCT_PROPERTIES: return "Number of distinct properties in dataset A.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Percentage of primary date formats in dataset A.";
            case NAMES_PERCENT: return "Percentage of name property in dataset A.";
            case WEBSITE_PERCENT: return "Percentage of website property in dataset A.";
            case EMAIL_PERCENT: return "Percentage of e-mail property in dataset A.";
            case PHONES_PERCENT: return "Percentage of phone property in dataset A.";
            case STREETS_PERCENT: return "Percentage of streets property in dataset A.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street number property in dataset A.";
            case LOCALITY_PERCENT: return "Percentage of locality property in dataset A.";
            case DATES_PERCENT: return "Percentage of date property in dataset A.";
            case LINKED_POIS: return "Linked POIs from dataset A.";
            case LINKED_VS_TOTAL: return "Linked POIs.";
            case LINKED_TRIPLES: return "Linked triples from A.";
            case LINKED_NON_EMPTY_NAMES: return "Number of linked POIs that have the name property in dataset A.";
            default: throw new IllegalArgumentException();
        }
    }
    
    public String getLegendB() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Number of POIs in B.";
            case TOTAL_TRIPLES: return "Number of triples in B.";
            case NON_EMPTY_NAMES: return "Total number of POIs that have the name property in dataset B.";
            case NON_EMPTY_PHONES: return "Total number of POIs that have the phone property in dataset B.";
            case NON_EMPTY_STREETS: return "Total number of POIs that have the address street property in dataset B.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in dataset B.";
            case NON_EMPTY_WEBSITES: return "Total number of POIs that have the website property in dataset B.";
            case NON_EMPTY_EMAILS: return "Total number of POIs that have the email property in dataset B.";
            case NON_EMPTY_DATES: return "Total number of POIs that have the date property in dataset B.";
            case EMPTY_NAMES: return "Total number of POIs that don' t have the name property in dataset B.";
            case EMPTY_PHONES: return "Total number of POIs that don' t have the phone property in dataset B.";
            case EMPTY_STREETS: return "Total number of POIs that don' t have the address street property in dataset B.";
            case EMPTY_STREET_NUMBERS: return "Total number of POIs that don' t have the address street number property in dataset B.";
            case EMPTY_WEBSITES: return "Total number of POIs that don' t have the website property in dataset B.";
            case EMPTY_EMAILS: return "Total number of POIs that don' t have the email property in dataset B.";
            case EMPTY_DATES: return "Total number of POIs that don' t have the date property in dataset B.";
            case DISTINCT_PROPERTIES: return "Number of distinct properties in dataset B.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Percentage of primary date formats in dataset B.";
            case NAMES_PERCENT: return "Percentage of name property in dataset B.";
            case WEBSITE_PERCENT: return "Percentage of website property in dataset B.";
            case EMAIL_PERCENT: return "Percentage of e-mail property in dataset B.";
            case PHONES_PERCENT: return "Percentage of phone property in dataset B.";
            case STREETS_PERCENT: return "Percentage of street property in dataset B.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street number property in dataset B.";
            case LOCALITY_PERCENT: return "Percentage of locality property in dataset B.";
            case DATES_PERCENT: return "Percentage of dates property in dataset B.";
            case LINKED_POIS: return "Linked POIs from dataset B.";
            case LINKED_VS_TOTAL: return "Total POIs.";
            case LINKED_TRIPLES: return "Linked triples from B.";
            case LINKED_NON_EMPTY_NAMES: return "Number of linked POIs that have the name property in dataset B.";
            default: throw new IllegalArgumentException();
        }
    }      
}
