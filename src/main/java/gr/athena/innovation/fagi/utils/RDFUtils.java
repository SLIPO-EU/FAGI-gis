package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class RDFUtils {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RDFUtils.class);
    
    public static String getIdFromResource(String resourceString) {

        int startPosition = StringUtils.ordinalIndexOf(resourceString, "/", 5) + 1;
        String id = resourceString.subSequence(startPosition, resourceString.length()).toString();

        return id;
    }

    public static String getIdFromResource(Resource resource) {

        String resourceString = resource.toString();
        int startPosition = StringUtils.ordinalIndexOf(resourceString, "/", 5) + 1;
        String id = resourceString.subSequence(startPosition, resourceString.length()).toString();

        return id;
    }

    public static String getIdFromResourcePart(String resourcePart) {
        //expects: <namespace:id> or <namespace:id/localname>
        int endPosition = StringUtils.lastIndexOf(resourcePart, "/");
        int startPosition = StringUtils.ordinalIndexOf(resourcePart, "/", 5) + 1;
        String res;
        if(resourcePart.substring(startPosition).contains("/")){
            res = resourcePart.subSequence(startPosition, endPosition).toString();
        } else {
            res = resourcePart.subSequence(startPosition, resourcePart.length()-1).toString();
        }

        return res;
    }

    public static String getLocalName(CustomRDFProperty property) {
        String localName;
        if(property.isSingleLevel()){
            localName = SpecificationConstants.Mapping.PROPERTY_MAPPINGS.get(property.getValueProperty().toString());
        } else {
            localName = SpecificationConstants.Mapping.PROPERTY_MAPPINGS.get(property.getParent().toString());
        }
        if(localName == null){
            LOG.warn("Failed to retrieve mapping with property " + property.getParent() + " " + property.getValueProperty());
            //do not stop fusion due to this
            //throw new ApplicationException("Property mapping does not exist.");
        }
        return localName;
    }
    
    public static String addBrackets(String node){
        return "<" + node + ">";
    }

    public static String removeBrackets(String node){
        return node.substring(1, node.length()-1);
    }

    public static Literal extractGeometry(Literal wkt){
        String lexicalForm = wkt.getLexicalForm();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        
        if(lexicalForm.startsWith(Namespace.CRS_4326)){
            lexicalForm = lexicalForm.replaceAll(Namespace.CRS_4326, "").trim();
            Literal wktLiteral = ResourceFactory.createTypedLiteral(lexicalForm, geometryDatatype);
            
            return wktLiteral;
        } else {
            return wkt;
        }
    }

    public static String extractGeometry(String wkt){

        if(wkt.startsWith(Namespace.CRS_4326)){
            String targetWKT = wkt.replaceAll(Namespace.CRS_4326, "").trim();
            return targetWKT;
        } else {
            return wkt;
        }
    }

    public static Resource getRootResource(Entity leftNode, Entity rightNode) {

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                return SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, leftNode.getEntityData().getModel());
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                return SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, rightNode.getEntityData().getModel());
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
    }
    
    public static Resource resolveResource(Entity leftNode, Entity rightNode, CustomRDFProperty property) {

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();
        Resource resource;
        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
            {
                resource = SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, leftNode.getEntityData().getModel());
                String localName = RDFUtils.getLocalName(property);
                if(localName == null){
                    return ResourceFactory.createResource(resource.toString());
                }
                String resourceString = resource.toString() +"/"+ RDFUtils.getLocalName(property);
                return ResourceFactory.createResource(resourceString);
            }
            case BB_MODE:
            case BA_MODE:
            case B_MODE:{
                resource = SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, rightNode.getEntityData().getModel());
                String localName = RDFUtils.getLocalName(property);
                if(localName == null){
                    return ResourceFactory.createResource(resource.toString());
                }
                String resourceString = resource.toString() +"/"+ RDFUtils.getLocalName(property);
                return ResourceFactory.createResource(resourceString);
            }
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
    }
}