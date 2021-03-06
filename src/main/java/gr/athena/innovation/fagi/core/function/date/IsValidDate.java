package gr.athena.innovation.fagi.core.function.date;

import gr.athena.innovation.fagi.core.function.IFunction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralStringParameters;

/**
 * Class for evaluating valid date strings against a date format. 
 * 
 * @author nkarag
 */
public class IsValidDate implements IFunction, IFunctionTwoLiteralStringParameters{
    
    /**
     * Validates the date range of the given date string using the lenient property of date.
     * 
     * @param date the date literal.
     * @param format the SimpleDateFormat of the date string
     * @return true if the date is valid and false if the date is invalid or it does not agree with the given format.
     */
    @Override
    public boolean evaluate(Literal date, String format) {
        
        if(date == null){
            return false;
        }

        //TODO - consider using https://github.com/joestelmach/natty for parsing unknown formats
        boolean isValid;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setLenient(false);
        try {
            
            simpleDateFormat.parse(date.getString());
            isValid = true;

        } catch (ParseException ex) {
            //LOG.error("Error parsing date: " + date + " with format: " + format);
            //LOG.error(ex);
            isValid = false;
        }

        return isValid;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
