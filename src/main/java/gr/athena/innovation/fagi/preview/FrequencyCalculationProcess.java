package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.Configuration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Class for calculating frequencies.
 * 
 * @author nkarag
 */
public class FrequencyCalculationProcess {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FrequencyCalculationProcess.class);

    /**
     * Executes the frequency calculation process.
     * 
     * @param configuration the configuration object.
     * @param rdfProperties the list of the RDF properties as String values.
     */
    public void run(Configuration configuration, List<String> rdfProperties) {

        //word frequencies using the RDF properties from file
        int topK = 0; //topK zero and negative values return the complete list

        //Frequent terms
        FileFrequencyCounter termFrequency = new FileFrequencyCounter(topK);
        termFrequency.setLocale(configuration.getLocale());

        termFrequency.setProperties(rdfProperties);

        termFrequency.export(configuration.getPathDatasetA(), EnumDataset.LEFT);
        
        termFrequency.export(configuration.getPathDatasetB(), EnumDataset.RIGHT);

        if (!StringUtils.isBlank(configuration.getCategoriesA())) {
            FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
            frequencyExtractor.extract(topK, configuration.getCategoriesA(), LeftDataset.getLeftDataset().getModel(),
                    configuration.getOutputDir(), configuration.getLocale(), EnumDataset.LEFT);
        }

        if (!StringUtils.isBlank(configuration.getCategoriesB())) {

            FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
            frequencyExtractor.extract(topK, configuration.getCategoriesB(), RightDataset.getRightDataset().getModel(),
                    configuration.getOutputDir(), configuration.getLocale(), EnumDataset.RIGHT);
        }
    }
}
