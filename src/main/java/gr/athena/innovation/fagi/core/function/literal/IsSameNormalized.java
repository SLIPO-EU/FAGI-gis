package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import gr.athena.innovation.fagi.core.function.IFunctionTwoStringParameters;

/**
 *
 * @author nkarag
 */
public class IsSameNormalized implements IFunction, IFunctionTwoStringParameters{
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameNormalized.class);
    
    /**
     * Compares the two literals and returns true if they are same. 
     * If the standard equals is not true, it normalizes the literals using th MultipleGenericNormalizer and re-checks.
     * 
     * @param literalA the literal A
     * @param literalB the literal B
     * @return true if the literals are found same before or after normalization.
     */
    @Override
    public boolean evaluate(String literalA, String literalB) {
        
        if(StringUtils.isBlank(literalA) || StringUtils.isBlank(literalB)){
            return false;
        }
        
        if(literalA.equals(literalB)){
            return true;
        }

        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();
        
        String a = normalizer.normalize(literalA, literalB);
        String b = normalizer.normalize(literalB, literalA);
        
        return a.equals(b);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }   
}
