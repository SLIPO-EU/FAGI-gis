package gr.athena.innovation.fagi.core.normalizer.phone;

import org.apache.commons.lang3.StringUtils;
import gr.athena.innovation.fagi.core.normalizer.INormalizer;

/**
 * Class for phone number normalization.
 * 
 * @author nkarag
 */
public class PhoneNumberNormalizer implements INormalizer{
    
    /**
     * Normalizes a telephone number representation by keeping only the numeric characters. 
     * The plus symbol for international telephone numbers gets removed if exitCodeDigits is null or empty.
     * 
     * @param numberString the string phone number.
     * @param exitCodeDigits digits to replace the "+" symbol in an international telephone number. 
     * @return the String representation of the normalized telephone number.
     */
    public String normalize(String numberString, String exitCodeDigits) {
        String normalizedNumber;

        if(isParsable(numberString)){
            return numberString;
            
        } else {

            if(numberString.startsWith("+")){
                if(StringUtils.isBlank(exitCodeDigits)){
                    normalizedNumber = removeNonNumericCharacters(numberString);
                } else {
                    String numberZeroReplaced = numberString.replaceAll("\\+", exitCodeDigits);
                    normalizedNumber = removeNonNumericCharacters(numberZeroReplaced);                    
                }
            } else {
                normalizedNumber = removeNonNumericCharacters(numberString);
            }
        }
        return normalizedNumber;
    }

    /**
     * Removes the non-numeric characters of the given phone number as string.  
     * 
     * @param number the phone number as string.
     * @return a new string that contains only numeric characters.
     */
    public static String removeNonNumericCharacters(String number){
        
        String numberNumerical = number.replaceAll("[^0-9]", "");
        
        return numberNumerical;
    }
    
    private boolean isParsable(String number){
        if(number == null){
            return false;
        }
        
        if(StringUtils.isBlank(number)){
            return false;
        }
        
        boolean parsable = true;
        
        try {
            Integer.parseInt(number);
        }catch(NumberFormatException e){
            //LOG.debug("Number is not parsable, but it is ok. \n");
            parsable = false;
        }
        return parsable;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
