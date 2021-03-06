package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumberTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSamePhoneNumberTest.class);

    /**
     * Test of evaluate method, of class IsSamePhoneNumber.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        String number1 = "0123456789";
        Literal literal1 = ResourceFactory.createStringLiteral(number1);
        String number2 = "0123456789";
        Literal literal2 = ResourceFactory.createStringLiteral(number2);
        boolean expResult1 = true;
        boolean result1 = isSamePhoneNumber.evaluate(literal1, literal2);
        assertEquals(expResult1, result1);

        String number3 = "+00123-44 5678 999";
        Literal literal3 = ResourceFactory.createStringLiteral(number3);
        String number4 = "+00123-44 5678 999";
        Literal literal4 = ResourceFactory.createStringLiteral(number4);
        boolean expResult2 = true;
        boolean result2 = isSamePhoneNumber.evaluate(literal3, literal4);
        assertEquals(expResult2, result2);

    }

    /**
     * Test of getName method, of class IsSamePhoneNumber.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        String expResult = SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER;
        String result = isSamePhoneNumber.getName();
        assertEquals(expResult, result);
    }
}
