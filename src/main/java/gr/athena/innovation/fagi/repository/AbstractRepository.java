package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.utils.RDFUtils;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract repository for reading RDF input. 
 * 
 * @author nkarag
 */
public abstract class AbstractRepository {

    private static final Logger LOG = LogManager.getLogger(AbstractRepository.class);
    
    private static int initialCount;
    
    /**
     * Loads the given RDF file into a RDF model as the left dataset.
     * 
     * @param filepath The path of the file.
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Indicates wrong input.
     */
    public abstract void parseLeft(String filepath) throws WrongInputException;
    
    /**
     * Loads the given RDF file into a RDF model as the right dataset.
     * @param filepath The path of the file.
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Indicates wrong input.
     */  
    public abstract void parseRight(String filepath) throws WrongInputException;
    
    /**
     * Loads the given RDF file into a RDF model as the fused dataset.
     * @param filepath The path of the file.
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Indicates wrong input.
     */  
    public abstract void parseFused(String filepath) throws WrongInputException;

    /**
     * Loads the given links file into a RDF model.
     * 
     * @param filepath The path of the file.
     * @throws java.text.ParseException if link file contains invalid links
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Indicates wrong input.
     */    
    public abstract void parseLinks(String filepath) throws ParseException, WrongInputException;

    /**
     * Parses the given RDF link file into a list of <code>Link</code> objects.
     * 
     * @param linksFile The file path of the links.
     * @return List of link objects.
     * @throws java.text.ParseException Exception parsing the file.
     */    
    public static List<Link> parseLinksFile(final String linksFile) throws ParseException {

        List<Link> links = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, linksFile);

        final StmtIterator iter = model.listStatements();

        while(iter.hasNext()) {

            final Statement statement = iter.nextStatement();
            final String nodeA = statement.getSubject().getURI();
            //jena getLocalName problem with URIs. Using custom implementation.
            final String uriA = RDFUtils.getIdFromResource(statement.getSubject().toString());
            final String nodeB;
            final String uriB;
            final RDFNode object = statement.getObject();

            if(object.isResource()) {
                nodeB = object.asResource().getURI();
                //jena getLocalName problem with URIs. Using custom implementation.
                uriB= RDFUtils.getIdFromResource(object.toString());
            } else {
                throw new ParseException("Failed to parse link (object not a resource): " + statement.toString(), 0);
            }

            Link link = new Link(nodeA, uriA, nodeB, uriB);

            initialCount++;

            links.add(link);
        }

        return links;       
    }

    /**
     * Return the count of the initial links.
     * 
     * @return the initial links count.
     */
    public static int getInitialCount() {
        return initialCount;
    }
}
