package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionSingleParameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Literal fusion class. Contains methods for checking and transforming literal values.
 * 
 * @author nkarag
 */
public class IsLiteralAbbreviation implements IFunction, IFunctionSingleParameter{
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsLiteralAbbreviation.class);
    
    /**
     * Checks if the given literal is an abbreviation by using a regular expression from the SpecificationConstants.
     * 
     * @param literal
     * @return returns true if the literal matches the pattern of regular expression that represents an abbreviation
     */
    @Override
    public boolean evaluate(String literal){
        logger.trace("Evaluating literal: " + literal);
        //1) check dictionary/wordsList
        
        //2) https://stackoverflow.com/questions/7331462/check-if-a-string-is-a-possible-abbrevation-for-a-name
        //>The first letter of the abbreviation must match the first letter of the text
        
        //The rest of the abbreviation (the abbrev minus the first letter) must be an abbreviation for:
        //
        //    the remaining words, or
        //    the remaining text starting from any position in the first word.
        
        //3)
        //Abbreviations (usually):
        //> Are not morphologically well-formed words
        //> Infringe upon the phonotactics of the language in which they occur 
        //> Employ punctuation marks, predominantly the period "." , within them
        //> Have the same collocations as their unabbreviated counterparts 
        //They also might:
        //> Use atypical alphanumeric characters such as /, & or ~
        //> Resemble a phonetic transcription of their unabbreviated counterparts
        //> Exploit the rebus principle (eg. inb4 "in before", NRG "energy")       

        if (!StringUtils.isBlank(literal)) {
            AbbreviationResolver resolver = AbbreviationResolver.getInstance();
            return resolver.containsAbbreviation(literal);
        } else {
            return false;
        }
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}