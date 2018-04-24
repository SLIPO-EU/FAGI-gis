package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.EntityData;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fusion core class. Contains methods for the fusion process.
 * 
 * @author nkarag
 */
public class Fuser implements IFuser{ 
    
    private static final Logger logger = LogManager.getLogger(Fuser.class);
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;

    /**
     * Fuses all links using the Rules from file.
     * 
     * @param fusionSpec
     * @param ruleCatalog
     * @param functionMap
     * @throws ParseException
     */
    @Override
    public List<LinkedPair> fuseAll(FusionSpecification fusionSpec, RuleCatalog ruleCatalog, 
            Map<String, IFunction> functionMap) throws ParseException, WrongInputException{

        List<LinkedPair> fusedList = new ArrayList<>();

        linkedEntitiesNotFoundInDataset = 0;

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        EnumOutputMode mode = fusionSpec.getOutputMode();

        for (Link link : links.getLinks()){
   
            //create the jena models for each node of the pair and remove them from the source models.
            Model modelA = constructEntityDataModel(link.getNodeA(), left, fusionSpec.getOptionalDepth());
            Model modelB = constructEntityDataModel(link.getNodeB(), right, fusionSpec.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            LinkedPair pair = new LinkedPair();

            String leftURI = link.getNodeA();
            String rightURI = link.getNodeB();
            
            Entity entityA = constructEntity(modelA, leftURI);
            Entity entityB = constructEntity(modelB, rightURI);

            pair.setLeftNode(entityA);
            pair.setRightNode(entityB);

            EnumValidationAction validation = pair.validateLink(ruleCatalog.getValidationRules(), functionMap);

            Entity newFusedEntity = new Entity();

            String targetURI = resolveURI(mode, leftURI, rightURI);
            
            newFusedEntity.setResourceURI(targetURI);

            pair.setFusedEntity(newFusedEntity);
            
            pair.fusePair(ruleCatalog, functionMap, validation);
            
            fusedList.add(pair);
            
            fusedPairsCount++;
        }
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);
        
        return fusedList;
    }
    
    /**
     * Constructs the output result by creating a new graph to the specified output 
     * or combining the fused entities into the source datasets.
     * 
     * @param fusionSpecification
     * @param fusedEntities
     * @param defaultDatasetAction
     * @throws FileNotFoundException
     */
    public void combineFusedAndWrite(FusionSpecification fusionSpecification, 
            List<LinkedPair> fusedEntities, EnumDatasetAction defaultDatasetAction) 
                    throws FileNotFoundException{
        
        OutputStream out;
        if(fusionSpecification.getPathOutput().equalsIgnoreCase("System.out")){
            out = System.out;
        } else {
            out = new FileOutputStream(fusionSpecification.getPathOutput());
        }

        switch(fusionSpecification.getOutputMode()) {
            case AA_MODE:
            {
                logger.warn("AA_MODE, writing to " + fusionSpecification.getPathOutput());
                Model leftModel = LeftModel.getLeftModel().getModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    
                    leftModel.add(fusedDataModel);
                }

                leftModel.write(out, fusionSpecification.getOutputRDFFormat());                
                break;
            }
            case BB_MODE:
            {
                logger.warn("BB_MODE, writing to " + fusionSpecification.getPathOutput());
                Model rightModel = RightModel.getRightModel().getModel();
                
                for(LinkedPair p : fusedEntities){

                    Model fusedModel = p.getFusedEntity().getEntityData().getModel();
                    rightModel.add(fusedModel);
                }

                rightModel.write(out, fusionSpecification.getOutputRDFFormat());              
                break;
            }
            case L_MODE:
            {
                logger.warn("L_MODE, writing to " + fusionSpecification.getPathOutput());
                //user default is NEW dataset.
                Model newModel = ModelFactory.createDefaultModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedModel = pair.getFusedEntity().getEntityData().getModel();
                    newModel.add(fusedModel);
                }

                newModel.write(out, fusionSpecification.getOutputRDFFormat());              
                break; 
            }
            case AB_MODE:
            {
                logger.warn("AB_MODE, writing to " + fusionSpecification.getPathOutput());
                Model leftModel = LeftModel.getLeftModel().getModel();
                Model rightModel = RightModel.getRightModel().getModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    leftModel.add(fusedDataModel);
                    //rightModel
                }

                leftModel.write(out, fusionSpecification.getOutputRDFFormat());                  
                //throw new UnsupportedOperationException("Not supported yet.");
                break;
            }
            case BA_MODE:
                logger.warn("AB_MODE, writing to " + fusionSpecification.getPathOutput());
                Model leftModel = LeftModel.getLeftModel().getModel();
                Model rightModel = RightModel.getRightModel().getModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    rightModel.add(fusedDataModel);
                    //rightModel
                }

                leftModel.write(out, fusionSpecification.getOutputRDFFormat());   
                break;
            case A_MODE:
                throw new UnsupportedOperationException("Not supported yet.");
                //break;
            case B_MODE:
                throw new UnsupportedOperationException("Not supported yet.");
                //break;
            default:
                throw new UnsupportedOperationException("Wrong Output mode!");
                //break;                
        }
    }
    
    private Entity constructEntity(Model model, String resourceURI) throws ParseException {
        
        Entity entity = new Entity();
        EntityData entityData = new EntityData(model);
        entity.setResourceURI(resourceURI);
        entity.setEntityData(entityData);
        
        return entity;
    }

    //creates a jena rdf model for this node and removes the node from the source dataset
    private Model constructEntityDataModel(String node, Model sourceModel, int depth){

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct(); //todo - maybe exclude geometry from model

        StmtIterator jenaIterator = model.listStatements();

        //SourceModel.remove(model) does not remove the model from the source for some reason.
        //Also, strange concurrent modification exception when using jena statement iterator. Using list iterator instead.
        List<Statement> stList = jenaIterator.toList();
        Iterator<Statement> stIterator = stList.iterator();
        while ( stIterator.hasNext() ) {
            Statement st = stIterator.next();
            sourceModel.remove(st);
        }         

        return model;
    }

    private String resolveURI(EnumOutputMode mode, String leftURI, String rightURI) {
        String resourceURI;
        switch (mode) {
            case AA_MODE:
                resourceURI = leftURI;
                break;
            case BB_MODE:
                resourceURI = rightURI;
                break;
            case AB_MODE:
                resourceURI = leftURI;
                break;    
            case BA_MODE:
                resourceURI = rightURI;
                break; 
            case A_MODE:
                resourceURI = leftURI;
                break; 
            case B_MODE:
                resourceURI = rightURI;
                break; 
            case L_MODE:
                resourceURI = leftURI;
                break;
            default:
                logger.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
        return resourceURI;
    }
    
    /**
     * Returns the count of linked entities that were not found in the source datasets.
     * 
     * @return
     */
    public int getLinkedEntitiesNotFoundInDataset() {
        return linkedEntitiesNotFoundInDataset;
    }

    /**
     *
     * @param linkedEntitiesNotFoundInDataset
     */
    public void setLinkedEntitiesNotFoundInDataset(int linkedEntitiesNotFoundInDataset) {
        this.linkedEntitiesNotFoundInDataset = linkedEntitiesNotFoundInDataset;
    }

    /**
     * Returns the total fused entities.
     * 
     * @return
     */
    public int getFusedPairsCount() {
        return fusedPairsCount;
    }

}
