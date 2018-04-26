package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;

/**
 * Class holding information about the left dataset of the specification.
 * 
 * @author nkarag
 */
public final class LeftDataset {
    
    private static LeftDataset leftDataset = null;
    private Model model;
    private String namespace;
    private String filepath;

    private LeftDataset() {
         //defeat instantiation
    }
    
    public static LeftDataset getLeftDataset() {
       if(leftDataset == null) {
          leftDataset = new LeftDataset();
       }
       return leftDataset;
    }
    
    public void setModel(Model model){
        this.model = model;
    }
    
    public Model getModel(){
        return model;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
}
