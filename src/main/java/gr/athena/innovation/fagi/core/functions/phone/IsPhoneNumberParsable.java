package gr.athena.innovation.fagi.core.functions.phone;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionSingleParameter;

/**
 *
 * @author nkarag
 */
public class IsPhoneNumberParsable implements IFunction, IFunctionSingleParameter{
    
    /**
     * Checks if the given number is represented as an integer. 
     * (Contains only numeric characters and no other symbols or spaces)
     * 
     * @param number
     * @return true if the telephone number representation can be parsed as an integer and false otherwise.
     * 
     */
    @Override
    public boolean evaluate(String number){
        
        boolean parsable = true;
        
        try {
            
            Integer.parseInt(number);
            
        }catch(NumberFormatException e){
            //logger.debug("Number is not parsable, but it is ok. \n");
            parsable = false;
        }           

        return parsable;
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}
