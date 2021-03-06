package gr.athena.innovation.fagi.core.similarity;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class LevenshteinTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LevenshteinTest.class);

    /**
     * Test of compute method, of class LevenshteinDistance.
     */
    @Test
    public void testComputeDistance() {
        LOG.info("compute");
        
        String a = "the first string for the test is longer than the second and all its characters are different.";
        String b = "###$$%%";
        double expResult1 = 1.0;
        double result1 = Levenshtein.computeDistance(a, b, null);
        assertEquals(expResult1, result1, 0.0);

        String c = "we are same!";
        String d = "we are same!";
        double expResult2 = 0.0;
        double result2 = Levenshtein.computeDistance(c, d, null);
        assertEquals(expResult2, result2, 0.0);
        
        String e = "one";
        String f = "one+";
        double expResult3 = 0.25; //1 levenshten distance / 4 max length of the two strings
        double result3 = Levenshtein.computeDistance(e, f, null);
        assertEquals(expResult3, result3, 0.0);
    }
}
