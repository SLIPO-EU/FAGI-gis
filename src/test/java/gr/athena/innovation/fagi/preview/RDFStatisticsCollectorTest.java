package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.ByteArrayInputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 *
 * @author nkarag
 */
public class RDFStatisticsCollectorTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RDFStatisticsCollectorTest.class);
    
    private Model modelA;
    private Model modelB;
    private Model linksModel;
    private final String datasetA;
    private final String datasetB;
    private final String links;

    public RDFStatisticsCollectorTest() {

        datasetA = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-A\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(9.60 47.32)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/name> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/name> <http://slipo.eu/def#nameValue> \"Michail Foufoutos\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#phone> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/phone> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/phone> <http://slipo.eu/def#contactValue> \"0123456789\" ."
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#email> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> <http://slipo.eu/def#contactValue> \"email_example@mail.com\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> <http://slipo.eu/def#contactType> \"email\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#contact> ."
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-A\" .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/geom> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(9.61 47.34)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/name> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/name> <http://slipo.eu/def#nameValue> \"Med. Foufoutos\" .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/name> <http://slipo.eu/def#nameType> \"official\" .";

        datasetB = "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-B\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/geom> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT (9.63 47.45)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> <http://slipo.eu/def#nameValue> \"Michail Foufoutos\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> <http://slipo.eu/def#language> \"en\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> <http://slipo.eu/def#nameType> \"official\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#homepage> <http://www.website-example.com> ."
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#address> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/address> ."
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/address> <http://slipo.eu/def#street> \"Street Name\"@en ."
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/address> <http://slipo.eu/def#number> \"11\" ."
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-B\" .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/geom> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT (9.65 47.42)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/geosparql#Feature> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://slipo.eu/def#nameValue> \"Dr.Med. Fouf\"@en .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://slipo.eu/def#language> \"en\" .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://slipo.eu/def#nameType> \"official\" .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#name> .";

        links = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.w3.org/2002/07/owl#sameAs> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> .";

    }

    @BeforeClass
    public static void setUpClass() {
        
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

        modelA = ModelFactory.createDefaultModel();
        modelA.read(new ByteArrayInputStream(datasetA.getBytes()), null, "N-TRIPLES");

        modelB = ModelFactory.createDefaultModel();
        modelB.read(new ByteArrayInputStream(datasetB.getBytes()), null, "N-TRIPLES");

        linksModel = ModelFactory.createDefaultModel();
        linksModel.read(new ByteArrayInputStream(links.getBytes()), null, "N-TRIPLES");
        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of collect method, of class RDFStatisticsCollector.
     */
//    @Test
//    public void testCollect() {
//        System.out.println("collect");
//        
//        StatisticsContainer expResult = null;
//        StatisticsContainer result = collector.collect();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    /**
     * Test of countTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTriples() {
        LOG.info("countTriples");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countTriples(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("23", "26");
        expResult.setLabel("Total triples");
        assertEquals(expResult, result);
    } 
    
    /**
     * Test of countTotalEntities method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalEntities() {
        LOG.info("countTotalEntities");
        
        StatisticResultPair expResult = new StatisticResultPair("2", "2");
        expResult.setLabel("Total POIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countTotalEntities(modelA, modelB);
        
        assertEquals(expResult, result);

    }
    
    /**
     * Test of countNonEmptyNames method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyNames() {
        LOG.info("countNonEmptyNames");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyNames(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("2", "2");
        expResult.setLabel("Non empty Names");
        assertEquals(expResult, result);
    }    

    /**
     * Test of countNonEmptyPhones method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyPhones() {
        LOG.info("countNonEmptyPhones");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyPhones(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("1", "0");
        expResult.setLabel("Non empty Phones");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyStreets method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyStreets() {
        LOG.info("countNonEmptyStreets");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyStreets(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0", "1");
        expResult.setLabel("Non empty Streets");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyStreetNumbers method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyStreetNumbers() {
        LOG.info("countNonEmptyStreetNumbers");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyStreetNumbers(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0", "1");
        expResult.setLabel("Non empty Street Numbers");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyWebsites method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyWebsites() {
        LOG.info("countNonEmptyWebsites");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyWebsites(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0", "1");
        expResult.setLabel("Non empty Websites");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyEmails method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyEmails() {
        LOG.info("countNonEmptyEmails");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyEmails(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("1", "0");
        expResult.setLabel("Non empty Emails");
        assertEquals(expResult, result);
    }    
    /**
     * Test of countNonEmptyDates method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyDates() {
        LOG.info("countNonEmptyDates");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyDates(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Non empty Dates");
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyNames method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyNames() {
        LOG.info("countEmptyNames");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Names");
        
        collector.getMap().put("nonEmptyNames", stat);
        
        StatisticResultPair result = collector.countEmptyNames();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Names");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countEmptyPhones method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyPhones() {
        LOG.info("countEmptyPhones");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Phones");
        
        collector.getMap().put("nonEmptyPhones", stat);
        
        StatisticResultPair result = collector.countEmptyPhones();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Phones");
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyStreets method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyStreets() {
        LOG.info("countEmptyStreets");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Streets");
        
        collector.getMap().put("nonEmptyStreets", stat);
        
        StatisticResultPair result = collector.countEmptyStreets();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Streets");
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyStreetNumbers method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyStreetNumbers() {
        LOG.info("countEmptyStreetNumbers");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Street Numbers");
        
        collector.getMap().put("nonEmptyStreetNumbers", stat);
        
        StatisticResultPair result = collector.countEmptyStreetNumbers();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Street Numbers");
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyWebsites method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyWebsites() {
        LOG.info("countEmptyWebsites");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Websites");
        
        collector.getMap().put("nonEmptyWebsites", stat);
        
        StatisticResultPair result = collector.countEmptyWebsites();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Websites");
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyEmails method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyEmails() {
        LOG.info("countEmptyEmails");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Emails");
        
        collector.getMap().put("nonEmptyEmails", stat);
        
        StatisticResultPair result = collector.countEmptyEmails();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Emails");
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyDates method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyDates() {
        LOG.info("countEmptyDates");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2");
        stat.setLabel("Non empty Dates");
        
        collector.getMap().put("nonEmptyDates", stat);
        
        StatisticResultPair result = collector.countEmptyDates();
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Empty Dates");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countDistinctProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountDistinctProperties() {
        LOG.info("countDistinctProperties");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countDistinctProperties(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("12", "13");
        expResult.setLabel("Distinct Properties");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculatePercentageOfPrimaryDateFormats method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculatePercentageOfPrimaryDateFormats() {
        LOG.info("calculatePercentageOfPrimaryDateFormats");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.calculatePercentageOfPrimaryDateFormats(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0", "0");
        expResult.setLabel("Percentage of primary date formats");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateNamePercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateNamePercentage() {
        LOG.info("calculateNamePercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculateNamePercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("100.0", "100.0");
        expResult.setLabel("Percentage of names in each dataset");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateWebsitePercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateWebsitePercentage() {
        LOG.info("calculateWebsitePercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculateWebsitePercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0.0", "50.0");
        expResult.setLabel("Percentage of websites in each dataset");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculatePhonePercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculatePhonePercentage() {
        LOG.info("calculatePhonePercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculatePhonePercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("50.0", "0.0");
        expResult.setLabel("Percentage of phones in each dataset");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateStreetPercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateStreetPercentage() {
        LOG.info("calculateStreetPercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculateStreetPercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0.0", "50.0");
        expResult.setLabel("Percentage of streets in each dataset");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateStreetNumberPercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateStreetNumberPercentage() {
        LOG.info("calculateStreetNumberPercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculateStreetNumberPercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0.0", "50.0");
        expResult.setLabel("Percentage of street numbers in each dataset");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateLocalityPercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateLocalityPercentage() {
        LOG.info("calculateLocalityPercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculateLocalityPercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0.0", "0.0");
        expResult.setLabel("Percentage of locality in each dataset");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateDatePercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateDatePercentage() {
        LOG.info("calculateDatesPercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculateDatePercentage(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0.0", "0.0");
        expResult.setLabel("Percentage of dates in each dataset");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedPOIs() {
        LOG.info("countLinkedPOIs");

   

        StatisticResultPair expResult =  new StatisticResultPair("1", "1");
        expResult.setLabel("Linked POIs");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedPOIs(linksModel);
        assertEquals(expResult, result);
    }

    /**
     * Test of countLinkedVsTotalPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedVsTotalPOIs() {
        LOG.info("countLinkedVsTotalPOIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedVsTotalPOIs(linksModel, 2, 2);
        
        StatisticResultPair expResult = new StatisticResultPair("2", "4");
        expResult.setLabel("Linked vs Total POIS");
        
        assertEquals(expResult, result);

    }    
    
    /**
     * Test of countTotalLinkedTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalLinkedTriples() {
        LOG.info("countTotalLinkedTriples");
        
        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);     
        
        StatisticResultPair expResult = new StatisticResultPair("6", "5");
        expResult.setLabel("Linked Triples");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countTotalLinkedTriples(linkedA, linkedB);
        assertEquals(expResult, result);
    }

    /**
     * Test of computeNonEmptyLinkedProperty method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeNonEmptyLinkedProperty() {
        LOG.info("computeNonEmptyLinkedProperty");
        
        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel); 

        StatisticResultPair expResult1 = new StatisticResultPair("1", "1");
        StatisticResultPair expResult2 = new StatisticResultPair("1", "0");
        StatisticResultPair expResult3 = new StatisticResultPair("0", "1");
        StatisticResultPair expResult4 = new StatisticResultPair("0", "1");
        StatisticResultPair expResult5 = new StatisticResultPair("0", "1");
        StatisticResultPair expResult6 = new StatisticResultPair("1", "0");
        StatisticResultPair expResult7 = new StatisticResultPair("0", "0");
        
        expResult1.setLabel("Linked Non Empty Names");
        expResult2.setLabel("Linked Non Empty Phones");
        expResult3.setLabel("Linked Non Empty Streets");
        expResult4.setLabel("Linked Non Empty Street Numbers");
        expResult5.setLabel("Linked Non Empty Websites");
        expResult6.setLabel("Linked Non Empty Emails");
        expResult7.setLabel("Linked Non Empty Dates");          

        StatisticResultPair result1 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.NAME, Namespace.NAME_VALUE);
        StatisticResultPair result2 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.PHONE, Namespace.CONTACT_VALUE);
        StatisticResultPair result3 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET);
        StatisticResultPair result4 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET_NUMBER);
        StatisticResultPair result5 = RDFStatisticsCollector.computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.HOMEPAGE);
        StatisticResultPair result6 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.EMAIL, Namespace.CONTACT_VALUE);
        //StatisticResultPair result7 = RDFStatisticsCollector.computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.DATE, null);        

        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
        assertEquals(expResult3, result3);
        assertEquals(expResult4, result4);
        assertEquals(expResult5, result5);
        assertEquals(expResult6, result6);
        //assertEquals(expResult7, result7);

    }
    

//
//    /**
//     * Test of calculateTotalNonEmptyPropertiesPercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateTotalNonEmptyPropertiesPercentage() {
//        System.out.println("calculateTotalNonEmptyPropertiesPercentage");
//        
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = collector.calculateTotalNonEmptyPropertiesPercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }


    
//

//
//    /**
//     * Test of countTotalNonEmptyProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountTotalNonEmptyProperties() {
//        System.out.println("countTotalNonEmptyProperties");
//        
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = collector.countTotalNonEmptyProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countTotalEmptyProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountTotalEmptyProperties() {
//        System.out.println("countTotalEmptyProperties");
//        
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = collector.countTotalEmptyProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//
//    /**
//     * Test of countNonEmptyProperty method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyProperty() {
//        System.out.println("countNonEmptyProperty");
//        String property = "";
//        EnumDataset dataset = null;
//        
//        Integer expResult = null;
//        Integer result = collector.countNonEmptyProperty(property, dataset);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyPropertyChain method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyPropertyChain() {
//        System.out.println("countNonEmptyPropertyChain");
//        String property1 = "";
//        String property2 = "";
//        EnumDataset dataset = null;
//        
//        Integer expResult = null;
//        Integer result = collector.countNonEmptyPropertyChain(property1, property2, dataset);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeLinkStats method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeLinkStats() {
//        System.out.println("computeLinkStats");
//        
//        collector.computeLinkStats();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//
//    /**
//     * Test of computeEmptyLinkedProperty method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeEmptyLinkedProperty() {
//        System.out.println("computeEmptyLinkedProperty");
//        Integer nonEmptyA = null;
//        Integer nonEmptyB = null;
//        
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = collector.computeEmptyLinkedProperty(nonEmptyA, nonEmptyB);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeNonEmptyLinkedTotalProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeNonEmptyLinkedTotalProperties() {
//        System.out.println("computeNonEmptyLinkedTotalProperties");
//        
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = collector.computeNonEmptyLinkedTotalProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeEmptyLinkedTotalProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeEmptyLinkedTotalProperties() {
//        System.out.println("computeEmptyLinkedTotalProperties");
//        
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = collector.computeEmptyLinkedTotalProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateAveragePropertiesPerPOI method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateAveragePropertiesPerPOI() {
//        System.out.println("calculateAveragePropertiesPerPOI");
//        
//        collector.calculateAveragePropertiesPerPOI();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateAveragePropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateAveragePropertiesOfLinkedPOIs() {
//        System.out.println("calculateAveragePropertiesOfLinkedPOIs");
//        
//        collector.calculateAveragePropertiesOfLinkedPOIs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateAverageEmptyPropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateAverageEmptyPropertiesOfLinkedPOIs() {
//        System.out.println("calculateAverageEmptyPropertiesOfLinkedPOIs");
//        
//        collector.calculateAverageEmptyPropertiesOfLinkedPOIs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMap method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testGetMap() {
//        System.out.println("getMap");
//        
//        Map<String, StatisticResultPair> expResult = null;
//        Map<String, StatisticResultPair> result = collector.getMap();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
