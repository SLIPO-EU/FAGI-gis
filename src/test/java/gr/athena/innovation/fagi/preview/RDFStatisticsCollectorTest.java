package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.preview.statistics.EnumStatGroup;
import gr.athena.innovation.fagi.preview.statistics.StatGroup;
import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
    private final String linksText;
    private List<Link> links;

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

        linksText = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.w3.org/2002/07/owl#sameAs> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> .";

    }

    @Before
    public void setUp() throws ParseException {

        modelA = ModelFactory.createDefaultModel();
        modelA.read(new ByteArrayInputStream(datasetA.getBytes()), null, "N-TRIPLES");

        modelB = ModelFactory.createDefaultModel();
        modelB.read(new ByteArrayInputStream(datasetB.getBytes()), null, "N-TRIPLES");

        linksModel = ModelFactory.createDefaultModel();
        linksModel.read(new ByteArrayInputStream(linksText.getBytes()), null, "N-TRIPLES");

        links = new ArrayList<>();

        final StmtIterator iter = linksModel.listStatements();
        
        while(iter.hasNext()) {
            
            final Statement statement = iter.nextStatement();
            final String nodeA = statement.getSubject().getURI();
            final String uriA = statement.getSubject().getLocalName();
            final String nodeB;
            final String uriB;
            final RDFNode object = statement.getObject();

            if(object.isResource()) {
                nodeB = object.asResource().getURI();
                uriB = object.asResource().getLocalName();
            }
            else {
                throw new ParseException("Failed to parse link (object not a resource): " + statement.toString(), 0);
            }
            Link link = new Link(nodeA, uriA, nodeB, uriB);
            links.add(link);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of collect method, of class RDFStatisticsCollector.
     */
//    @Test
//    public void testCollect() {
//    }
    
    /**
     * Test of countTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTriples() {
        LOG.info("countTriples");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countTriples(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("23", "26", null);
        expResult.setTitle(EnumStat.TOTAL_TRIPLES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.TRIPLE_BASED));
        assertEquals(expResult, result);
    } 
    
    /**
     * Test of countTotalEntities method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalEntities() {
        LOG.info("countTotalEntities");
        
        StatisticResultPair expResult = new StatisticResultPair("2", "2", null);
        expResult.setTitle(EnumStat.TOTAL_POIS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        
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
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_NAMES, Namespace.NAME_VALUE);
        StatisticResultPair expResult = new StatisticResultPair("2", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_NAMES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        assertEquals(expResult, result);
    }    

    /**
     * Test of countNonEmptyPhones method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyPhones() {
        LOG.info("countNonEmptyPhones");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_PHONES, Namespace.PHONE);
        StatisticResultPair expResult = new StatisticResultPair("1", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_PHONES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyStreets method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyStreets() {
        LOG.info("countNonEmptyStreets");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_STREETS, Namespace.STREET);
        StatisticResultPair expResult = new StatisticResultPair("0", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREETS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyStreetNumbers method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyStreetNumbers() {
        LOG.info("countNonEmptyStreetNumbers");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER);
        StatisticResultPair expResult = new StatisticResultPair("0", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREET_NUMBERS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyWebsites method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyWebsites() {
        LOG.info("countNonEmptyWebsites");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_WEBSITES, Namespace.HOMEPAGE);
        StatisticResultPair expResult = new StatisticResultPair("0", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_WEBSITES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyEmails method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyEmails() {
        LOG.info("countNonEmptyEmails");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL);
        StatisticResultPair expResult = new StatisticResultPair("1", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_EMAILS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }    
    /**
     * Test of countNonEmptyDates method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyDates() {
        LOG.info("countNonEmptyDates");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_DATES, Namespace.DATE);
        StatisticResultPair expResult = new StatisticResultPair("0", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_DATES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyNames method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyNames() {
        LOG.info("countEmptyNames");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_NAMES, Namespace.NAME);
        StatisticResultPair expResult = new StatisticResultPair("0", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_NAMES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countEmptyPhones method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyPhones() {
        LOG.info("countEmptyPhones");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_PHONES, Namespace.PHONE);
        StatisticResultPair expResult = new StatisticResultPair("1", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_PHONES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyStreets method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyStreets() {
        LOG.info("countEmptyStreets");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_STREETS, Namespace.STREET);
        StatisticResultPair expResult = new StatisticResultPair("2", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREETS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyStreetNumbers method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyStreetNumbers() {
        LOG.info("countEmptyStreetNumbers");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER);
        StatisticResultPair expResult = new StatisticResultPair("2", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREET_NUMBERS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyWebsites method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyWebsites() {
        LOG.info("countEmptyWebsites");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_WEBSITES, Namespace.HOMEPAGE);
        StatisticResultPair expResult = new StatisticResultPair("2", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_WEBSITES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyEmails method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyEmails() {
        LOG.info("countEmptyEmails");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_EMAILS, Namespace.EMAIL);
        StatisticResultPair expResult = new StatisticResultPair("1", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_EMAILS.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyDates method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyDates() {
        LOG.info("countEmptyDates");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_DATES, Namespace.DATE);
        StatisticResultPair expResult = new StatisticResultPair("2", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_DATES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
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
        StatisticResultPair expResult = new StatisticResultPair("12", "13", null);
        expResult.setTitle(EnumStat.DISTINCT_PROPERTIES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of calculatePercentageOfPrimaryDateFormats method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculatePercentageOfPrimaryDateFormats() {
        LOG.info("calculatePercentageOfPrimaryDateFormats");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.calculatePercentageOfPrimaryDateFormats(modelA, modelB, EnumStat.PRIMARY_DATE_FORMATS_PERCENT);
        StatisticResultPair expResult = new StatisticResultPair(null, null, null);
        expResult.setTitle(EnumStat.UNDEFINED.toString() + " \"" + EnumStat.PRIMARY_DATE_FORMATS_PERCENT.toString() + "\"");
        expResult.setGroup(new StatGroup(EnumStatGroup.UNDEFINED));
        
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
        
        StatisticResultPair result = collector.calculatePropertyPercentage(modelA, modelB, EnumStat.NAMES_PERCENT, Namespace.NAME_VALUE);
        StatisticResultPair expResult = new StatisticResultPair("100.0", "100.0", null);
        expResult.setTitle(EnumStat.NAMES_PERCENT.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PERCENT));
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedPOIs() {
        LOG.info("countLinkedPOIs");

        StatisticResultPair expResult =  new StatisticResultPair("1", "1", null);
        expResult.setTitle(EnumStat.LINKED_POIS.toString());

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedPOIs(linksModel);
        expResult.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countLinkedVsTotalPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedVsTotalPOIs() {
        LOG.info("countLinkedVsTotalPOIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedVsTotalPOIs(modelA, modelB, linksModel);
        
        StatisticResultPair expResult = new StatisticResultPair("2", "4", null);
        expResult.setTitle(EnumStat.LINKED_VS_TOTAL.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        
        assertEquals(expResult, result);

    }    
    
    /**
     * Test of countTotalLinkedTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalLinkedTriples() {
        LOG.info("countTotalLinkedTriples");  
        
        StatisticResultPair expResult = new StatisticResultPair("6", "5", null);
        expResult.setTitle(EnumStat.LINKED_TRIPLES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.TRIPLE_BASED));
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedTriples(modelA, modelB, linksModel);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of computeNonEmptyLinkedProperty method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeNonEmptyLinkedProperty() {
        LOG.info("computeNonEmptyLinkedProperty");

        StatisticResultPair expResult1 = new StatisticResultPair("1", "1", null);
        StatisticResultPair expResult2 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult3 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult4 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult5 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult6 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult7 = new StatisticResultPair("0", "0", null);
        
        expResult1.setTitle(EnumStat.LINKED_NON_EMPTY_NAMES.toString());
        expResult1.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult2.setTitle(EnumStat.LINKED_NON_EMPTY_PHONES.toString());
        expResult2.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult3.setTitle(EnumStat.LINKED_NON_EMPTY_STREETS.toString());
        expResult3.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult4.setTitle(EnumStat.LINKED_NON_EMPTY_STREET_NUMBERS.toString());
        expResult4.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult5.setTitle(EnumStat.LINKED_NON_EMPTY_WEBSITES.toString());
        expResult5.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult6.setTitle(EnumStat.LINKED_NON_EMPTY_EMAILS.toString());
        expResult6.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult7.setTitle(EnumStat.LINKED_NON_EMPTY_DATES.toString());
        expResult7.setGroup(new StatGroup(EnumStatGroup.PROPERTY));

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result1 = collector.computeNonEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_NAMES, Namespace.NAME, Namespace.NAME_VALUE);
        result1.setTitle(EnumStat.LINKED_NON_EMPTY_NAMES.toString());
        
        StatisticResultPair result2 = collector.computeNonEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_PHONES, Namespace.PHONE, Namespace.CONTACT_VALUE);
        StatisticResultPair result3 = collector.computeNonEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_STREETS, Namespace.ADDRESS, Namespace.STREET);
        StatisticResultPair result4 = collector.computeNonEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_STREET_NUMBERS, Namespace.ADDRESS, Namespace.STREET_NUMBER);
        StatisticResultPair result5 = collector.computeNonEmptyLinkedProperty(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_WEBSITES, Namespace.HOMEPAGE);
        StatisticResultPair result6 = collector.computeNonEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_EMAILS, Namespace.EMAIL, Namespace.CONTACT_VALUE);
        StatisticResultPair result7 = collector.computeNonEmptyLinkedProperty(
                modelA, modelB, linksModel, EnumStat.LINKED_NON_EMPTY_DATES, Namespace.DATE);        

        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
        assertEquals(expResult3, result3);
        assertEquals(expResult4, result4);
        assertEquals(expResult5, result5);
        assertEquals(expResult6, result6);
        assertEquals(expResult7, result7);

    }

    /**
     * Test of computeEmptyLinkedProperty method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeEmptyLinkedProperty() {
        LOG.info("computeEmptyLinkedProperty");     
        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair result1 = collector.computeEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_NAMES, Namespace.NAME, Namespace.NAME_VALUE);
        StatisticResultPair result2 = collector.computeEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_PHONES, Namespace.PHONE, Namespace.CONTACT_VALUE);
        StatisticResultPair result3 = collector.computeEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_STREETS, Namespace.ADDRESS, Namespace.STREET);
        StatisticResultPair result4 = collector.computeEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_STREET_NUMBERS, Namespace.ADDRESS, Namespace.STREET_NUMBER);
        StatisticResultPair result5 = collector.computeEmptyLinkedProperty(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_WEBSITES, Namespace.HOMEPAGE);
        StatisticResultPair result6 = collector.computeEmptyLinkedPropertyChain(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_EMAILS, Namespace.EMAIL, Namespace.CONTACT_VALUE);
        StatisticResultPair result7 = collector.computeEmptyLinkedProperty(
                modelA, modelB, linksModel, EnumStat.LINKED_EMPTY_DATES, Namespace.DATE);  

        StatisticResultPair expResult1 = new StatisticResultPair("0", "0", null);
        StatisticResultPair expResult2 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult3 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult4 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult5 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult6 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult7 = new StatisticResultPair("1", "1", null);

        expResult1.setTitle(EnumStat.LINKED_EMPTY_NAMES.toString());
        expResult1.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult2.setTitle(EnumStat.LINKED_EMPTY_PHONES.toString());
        expResult2.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult3.setTitle(EnumStat.LINKED_EMPTY_STREETS.toString());
        expResult3.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult4.setTitle(EnumStat.LINKED_EMPTY_STREET_NUMBERS.toString());
        expResult4.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult5.setTitle(EnumStat.LINKED_EMPTY_WEBSITES.toString());
        expResult5.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult6.setTitle(EnumStat.LINKED_EMPTY_EMAILS.toString());
        expResult6.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        expResult7.setTitle(EnumStat.LINKED_EMPTY_DATES.toString());
        expResult7.setGroup(new StatGroup(EnumStatGroup.PROPERTY));

        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
        assertEquals(expResult3, result3);
        assertEquals(expResult4, result4);
        assertEquals(expResult5, result5);
        assertEquals(expResult6, result6);
        assertEquals(expResult7, result7);
    }

    /**
     * Test of countTotalNonEmptyProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalNonEmptyProperties() {
        LOG.info("countTotalNonEmptyProperties");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair expResult = new StatisticResultPair("4", "5", null);
        expResult.setTitle(EnumStat.TOTAL_NON_EMPTY_PROPERTIES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        StatisticResultPair result = collector.countTotalNonEmptyProperties(modelA, modelB);

        assertEquals(expResult, result);

    }

    /**
     * Test of countTotalEmptyProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalEmptyProperties() {
        LOG.info("countTotalEmptyProperties");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair expResult = new StatisticResultPair("10", "9", null);
        expResult.setTitle(EnumStat.TOTAL_EMPTY_PROPERTIES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        
        StatisticResultPair result = collector.countTotalEmptyProperties(modelA, modelB);

        assertEquals(expResult, result);
    }

    /**
     * Test of calculateTotalNonEmptyPropertiesPercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateTotalNonEmptyPropertiesPercentage() {
        LOG.info("calculateTotalNonEmptyPropertiesPercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair expResult = new StatisticResultPair("25.0", "31.25", null);
        expResult.setTitle(EnumStat.TOTAL_PROPERTIES_PERCENTAGE.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PERCENT));
        StatisticResultPair result = collector.calculateTotalNonEmptyPropertiesPercentage(modelA, modelB);
        
        assertEquals(expResult, result);

    }

    /**
     * Test of calculateAveragePropertiesPerPOI method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAveragePropertiesPerPOI() {
        LOG.info("calculateAveragePropertiesPerPOI");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair expResult = new StatisticResultPair("4.0", "4.5", null);
        expResult.setTitle(EnumStat.AVERAGE_PROPERTIES_PER_POI.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        StatisticResultPair result = collector.calculateAveragePropertiesPerPOI(modelA, modelB);

        assertEquals(expResult, result);
    }

    /**
     * Test of calculateAverageEmptyPropertiesPerPOI method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAverageEmptyPropertiesPerPOI() {
        LOG.info("calculateAverageEmptyPropertiesPerPOI");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair expResult = new StatisticResultPair("8.0", "8.5", null);
        expResult.setTitle(EnumStat.AVERAGE_EMPTY_PROPERTIES_PER_POI.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        StatisticResultPair result = collector.calculateAverageEmptyPropertiesPerPOI(modelA, modelB);

        assertEquals(expResult, result);
    }
    
    /**
     * Test of calculateAveragePropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAverageLinkedProperties() {
        LOG.info("calculateAverageLinkedProperties");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair expResult = new StatisticResultPair("2.5", "2.5", null);
        expResult.setTitle(EnumStat.LINKED_AVERAGE_PROPERTIES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        StatisticResultPair results = collector.calculateAverageLinkedProperties(modelA, modelB, links);
        
        assertEquals(expResult, results);
    }
    
    /**
     * Test of calculateAverageEmptyPropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAverageEmptyLinkedProperties() {
        LOG.info("calculateAverageEmptyPropertiesOfLinkedPOIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair expResult = new StatisticResultPair("9.5", "10.5", null);
        expResult.setTitle(EnumStat.LINKED_AVERAGE_EMPTY_PROPERTIES.toString());
        expResult.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        StatisticResultPair result = collector.calculateAverageEmptyLinkedProperties(modelA, modelB, links);

        assertEquals(expResult, result);
    }
}
