package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class AlphabeticalNormalizerTest {
    
    private static final org.apache.logging.log4j.Logger LOG 
            = LogManager.getLogger(AlphabeticalNormalizerTest.class);

    /**
     * Test of normalize method, of class AlphabeticalNormalizer.
     */
    @Test
    public void testNormalize() {
        LOG.info("normalize");
        
        AlphabeticalNormalizer alphabeticalNormalizer = new AlphabeticalNormalizer();
        
        String literal = "I am fagi";
        String expResult1 = "am fagi I";
        
        String result1 = alphabeticalNormalizer.normalize(literal);
        assertEquals(expResult1, result1);
    }

    /**
     * Test of getName method, of class AlphabeticalNormalizer.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        AlphabeticalNormalizer alphabeticalNormalizer = new AlphabeticalNormalizer();
        String expResult = SpecificationConstants.Normalize.NORMALIZE_ALPHABETICALLY;
        String result = alphabeticalNormalizer.getName();
        assertEquals(expResult, result);
    }
}
