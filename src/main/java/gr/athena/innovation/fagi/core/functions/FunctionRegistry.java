package gr.athena.innovation.fagi.core.functions;

import gr.athena.innovation.fagi.core.functions.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.functions.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.functions.property.Exists;
import gr.athena.innovation.fagi.core.functions.date.IsValidDate;
import gr.athena.innovation.fagi.core.functions.geo.IsGeometryMoreComplicated;
import gr.athena.innovation.fagi.core.functions.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.functions.phone.IsSamePhoneNumber;
import gr.athena.innovation.fagi.core.functions.phone.IsSamePhoneNumberUsingExitCode;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Registers all available functions that can be defined inside rule conditions.
 * 
 * @author nkarag
 */
public class FunctionRegistry {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FunctionRegistry.class);
    private boolean isInitialized = false;
    private HashMap<String, IFunction> functionMap;
    
    /**
     * Initializes a FunctionRegistry object. Creates all available function objects and puts them in the functionMap.
     * The function map contains key-value entries of function names along with their corresponding function object.
     * 
     */
    public void init(){

        functionMap = new HashMap<>();
        
        //date
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();
        IsValidDate isValidDate = new IsValidDate();

        //geo
        IsGeometryMoreComplicated isGeometryMoreComplicated = new IsGeometryMoreComplicated();
        
        //literal
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();

        //phone
        IsPhoneNumberParsable isPhoneNumberParsable = new IsPhoneNumberParsable();
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();
        
        //property
        Exists exists = new Exists();
        
        //register all functions
        functionMap.put(isDateKnownFormat.getName(), isDateKnownFormat);
        functionMap.put(isValidDate.getName(), isValidDate);

        //geo
        functionMap.put(isGeometryMoreComplicated.getName(), isGeometryMoreComplicated);
        
        //literal
        functionMap.put(isLiteralAbbreviation.getName(), isLiteralAbbreviation);
        
        //phone
        functionMap.put(isPhoneNumberParsable.getName(), isPhoneNumberParsable);
        functionMap.put(isSamePhoneNumber.getName(), isSamePhoneNumber);
        functionMap.put(isSamePhoneNumberUsingExitCode.getName(), isSamePhoneNumberUsingExitCode);
        
        //property
        functionMap.put(exists.getName(), exists);

        isInitialized = true;
    }
    
    /**
     * Returns the map that contains the function names as keys and the corresponding function objects as values.
     * 
     * @throws RuntimeException when the FunctionRegistry is not initialized.
     * @return the map
     */
    public Map<String, IFunction> getFunctionMap() {
        if(!isInitialized){
            logger.fatal("Method registry is not initialized.");
            throw new RuntimeException();
        } else {
            return functionMap;
        }
    }    
}