package gr.athena.innovation.fagi.core.function.property;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoParameters;
import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Checks if the provided property exists in the RDF model of a resource.
 * 
 * @author nkarag
 */
public class Exists implements IFunction, IFunctionTwoParameters{

    /**
     * This is the actual method that it is used to evaluate the existence of the property in the model.
     * 
     * @param modelText the RDF model as text input.
     * @param propertyString the property string.
     * @return true if the property exists in the model, false otherwise.
     */
    @Override
    public boolean evaluate(String modelText, String propertyString) {
        
        if(StringUtils.isBlank(propertyString)){
            return false;
        }
        
        //TODO: create test
        Property property = ResourceFactory.createProperty(propertyString);
        final Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(modelText.getBytes()), null);
        
        return propertyExistsInModel(model, property);

    }

    /**
     * This is the actual method that it is used to evaluate the existence of the property in the model.
     * 
     * @param model
     * @param propertyString
     * @return
     */
    public boolean evaluate(Model model, String propertyString) {
        Property property = ResourceFactory.createProperty(propertyString);
        return propertyExistsInModel(model, property);
    }
    
    /**
     *
     * @param model
     * @param property
     * @return
     */
    public static boolean propertyExistsInModel(Model model, Property property){

        for (StmtIterator i = model.listStatements( null, null, (RDFNode) null ); i.hasNext(); ) {

            Statement originalStatement = i.nextStatement();
            Property p = originalStatement.getPredicate();    
            if(p.equals(property)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}
