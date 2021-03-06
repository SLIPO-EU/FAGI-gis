package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class NGramTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(NGramTest.class);
    
    /**
     * Test of compute method, of class NGram.
     */
    @Test
    public void testCompute2Grams() {
        LOG.info("compute 2-grams");

        float expResult1 = 0.0F;
        float result1 = NGram.computeDistance("", "");
        //LOG.debug(result1);
        assertEquals(expResult1, result1, 0.0);

        float expResult2 = 0.0F;
        float result2 = NGram.computeDistance("twogram", "twogram");
        //LOG.debug(result2);
        assertEquals(expResult2, result2, 0.0);

        float expResult3 = 1.0F;
        float result3 = NGram.computeDistance("foo", "bar");
        //LOG.debug(result3);
        assertEquals(expResult3, result3, 0.0);
        
        String a = "This is a foo bar string.";
        String b = "This string is similar to a foo bar string.";        
        float expResult4 = 0.41F;
        float result4 = NGram.computeDistance(a, b);
        //LOG.debug(result4);
        assertEquals(expResult4, result4, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);
    }

    /**
     * Test of compute method, of class NGram.
     */
    @Test
    public void testComputeCustomSize() {
        
        LOG.info("compute N-grams of size " + 5);
        float expResult1 = 0.0F;
        float result1 = NGram.computeDistance("", "",5);
        //LOG.debug(result1);
        assertEquals(expResult1, result1, 0.0);

        LOG.info("compute N-grams of size " + 5);
        float expResult2 = 0.0F;
        float result2 = NGram.computeDistance("twogram", "twogram", 5);
        //LOG.debug(result2);
        assertEquals(expResult2, result2, 0.0);

        //LOG.info("compute N-grams of size " + 5);
        float expResult3 = 1.0F;
        float result3 = NGram.computeDistance("foo", "bar", 5);
        //LOG.debug(result3);
        assertEquals(expResult3, result3, 0.0);

        LOG.info("compute N-grams of size " + 10);
        String a = "This is a foo bar string.";
        String b = "This string is similar to a foo bar string.";        
        float expResult4 = 0.50F;
        float result4 = NGram.computeDistance(a, b, 10);
        //LOG.debug(result4);
        assertEquals(expResult4, result4, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);

        LOG.info("compute N-grams of size " + 4);
        String c = "This is a foo bar string.";
        String d = "This string is similar to a foo bar string.";        
        float expResult5 = 0.45F;
        float result5 = NGram.computeDistance(c, d, 4);
        //LOG.debug(result5);
        assertEquals(expResult5, result5, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);        

    }
    
}
