package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;

/**
 * Class for extracting category frequencies.
 * 
 * @author nkarag
 */
public class FrequencyExtractor {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FrequencyExtractor.class);

    /**
     * Extracts category frequencies.
     * 
     * @param frequentTopK the top-k value.
     * @param categoryMappingsNTPath the path containing the mappings in N-triples format.
     * @param model the RDF model.
     * @param outputDir the output directory.
     * @param locale the locale.
     * @param dataset the dataset enumeration value.
     */
    public void extract(int frequentTopK, String categoryMappingsNTPath, Model model, 
            String outputDir, Locale locale, EnumDataset dataset){

            //Category frequencies
            RDFFrequencyCounter categoryCounter = new RDFFrequencyCounter();

            Map<String, String> categoryMap = categoryCounter.getCategoryMap(categoryMappingsNTPath);

            Frequency categoryFrequencies = categoryCounter.exportCategoryFrequency(Namespace.CATEGORY, model);

            String filename;
            switch(dataset){
                case LEFT:
                    filename = "category.freq.A.txt";
                    break;
                case RIGHT:   
                    filename = "category.freq.B.txt";
                    break;
                default:
                    throw new ApplicationException("Wrong parameter for EnumDataset in Frequency extractor. "
                            + "Only LEFT or RIGHT allowed.");
            }

            String outputFilename = outputDir + "frequencies/" + filename;
            File outputFile = new File(outputFilename);

            LOG.info("Category frequency file: " + outputFile);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
                writer.append("# category frequencies");    
                writer.newLine();
                for (Map.Entry<String, Integer> f : categoryFrequencies.getTopKFrequency(frequentTopK).entrySet()){
                    String catLiteral = categoryMap.get(f.getKey());
                    
                    String pair = catLiteral + "=" + f.getValue();
                    writer.append(pair);
                    writer.newLine();

                }
            } catch (IOException ex) {
                LOG.error(ex);
                throw new ApplicationException(ex.getMessage());
        }        
    }
}
