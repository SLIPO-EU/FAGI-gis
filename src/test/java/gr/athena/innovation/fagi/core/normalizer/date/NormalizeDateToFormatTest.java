package gr.athena.innovation.fagi.core.normalizer.date;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class NormalizeDateToFormatTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(NormalizeDateToFormatTest.class);
    
    public NormalizeDateToFormatTest() {
    }

    /**
     * Test of transformDateToFormat method, of class TransformToFormat.
     */
    @Test
    public void testNormalize() {
        LOG.info("normalize");
        
        NormalizeDateToFormat transformToFormat = new NormalizeDateToFormat();
        
        String date1 = "19-09-2015";
        String targetFormat1 = "yyyy/mm/dd";
        
        String expResult1 = "2015/09/19";
        String result1 = transformToFormat.normalize(date1, targetFormat1);
        assertEquals(expResult1, result1);
        
        String date = "11/11/2015";
        String targetFormat2 = "yyyy mm dd";
        String expResult = "2015 11 11";
        String result = transformToFormat.normalize(date, targetFormat2);
        assertEquals(expResult, result);        
    }
    
    /**
     * Test of getName method, of class TransformToFormat.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        NormalizeDateToFormat transformToFormat = new NormalizeDateToFormat();
        String expResult = SpecificationConstants.Normalize.NORMALIZE_DATE_TO_FORMAT;
        String result = transformToFormat.getName();
        assertEquals(expResult, result);
    }    
}
