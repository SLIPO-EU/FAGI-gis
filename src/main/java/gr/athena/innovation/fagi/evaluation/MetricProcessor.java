package gr.athena.innovation.fagi.evaluation;

import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.Cosine;
import gr.athena.innovation.fagi.core.similarity.Jaccard;
import gr.athena.innovation.fagi.core.similarity.Jaro;
import gr.athena.innovation.fagi.core.similarity.JaroWinkler;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.core.similarity.LongestCommonSubsequenceMetric;
import gr.athena.innovation.fagi.core.similarity.NGram;
import gr.athena.innovation.fagi.core.similarity.SortedJaroWinkler;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for calculation of accuracy, precision, recall and harmonic mean for linked pairs and acceptance label.
 *
 * @author nkarag
 */
public class MetricProcessor {

    private static final Logger LOG = LogManager.getLogger(MetricProcessor.class);
    private static final String SEP = ",";
    private final Configuration configuration;
    private static final String ACCEPT = "ACCEPT";
    private static final String REJECT = "REJECT";
    private int totalRows = 0;
    
    Accuracy optimalAccuracy = new Accuracy();
    
    private Map<String, List<Double>> aLevenPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bLevenPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cLevenPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> dLevenPrecisionMap = new HashMap<>();
    
    private Map<String, List<Double>> aNGramPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bNGramPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cNGramPrecisionMap = new HashMap<>();    
    private Map<String, List<Double>> dNGramPrecisionMap = new HashMap<>();
    
    private Map<String, List<Double>> aCosinePrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bCosinePrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cCosinePrecisionMap = new HashMap<>(); 
    private Map<String, List<Double>> dCosinePrecisionMap = new HashMap<>();
    
    private Map<String, List<Double>> aLqsPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bLqsPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cLqsPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> dLqsPrecisionMap = new HashMap<>();

    private Map<String, List<Double>> aJaccardPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bJaccardPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cJaccardPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> dJaccardPrecisionMap = new HashMap<>();

    private Map<String, List<Double>> aJaroPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bJaroPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cJaroPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> dJaroPrecisionMap = new HashMap<>();

    private Map<String, List<Double>> aJaroWinklerPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bJaroWinklerPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cJaroWinklerPrecisionMap = new HashMap<>(); 
    private Map<String, List<Double>> dJaroWinklerPrecisionMap = new HashMap<>();

    private Map<String, List<Double>> aSortedJaroWinklerPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bSortedJaroWinklerPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cSortedJaroWinklerPrecisionMap = new HashMap<>(); 
    private Map<String, List<Double>> dSortedJaroWinklerPrecisionMap = new HashMap<>();
    
    private Threshold optimalThreshold = new Threshold();
    
    /**
     * Constructor of a metric processor object.
     * Initializes the hashmaps with accept/reject keys and arraylist values. 
     * 
     * @param configuration
     */
    public MetricProcessor(Configuration configuration) {

        this.configuration = configuration;
        
        aLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        aLevenPrecisionMap.put(REJECT, new ArrayList<>());
        bLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        bLevenPrecisionMap.put(REJECT, new ArrayList<>());
        cLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        cLevenPrecisionMap.put(REJECT, new ArrayList<>());
        dLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        dLevenPrecisionMap.put(REJECT, new ArrayList<>());
        
        aNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        aNGramPrecisionMap.put(REJECT, new ArrayList<>());
        bNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        bNGramPrecisionMap.put(REJECT, new ArrayList<>());
        cNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        cNGramPrecisionMap.put(REJECT, new ArrayList<>());
        dNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        dNGramPrecisionMap.put(REJECT, new ArrayList<>());
        
        aCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        aCosinePrecisionMap.put(REJECT, new ArrayList<>());
        bCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        bCosinePrecisionMap.put(REJECT, new ArrayList<>());
        cCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        cCosinePrecisionMap.put(REJECT, new ArrayList<>());
        dCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        dCosinePrecisionMap.put(REJECT, new ArrayList<>());
        
        aLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        aLqsPrecisionMap.put(REJECT, new ArrayList<>());
        bLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        bLqsPrecisionMap.put(REJECT, new ArrayList<>());
        cLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        cLqsPrecisionMap.put(REJECT, new ArrayList<>());        
        dLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        dLqsPrecisionMap.put(REJECT, new ArrayList<>()); 
        
        aJaccardPrecisionMap.put(ACCEPT, new ArrayList<>());
        aJaccardPrecisionMap.put(REJECT, new ArrayList<>());
        bJaccardPrecisionMap.put(ACCEPT, new ArrayList<>());
        bJaccardPrecisionMap.put(REJECT, new ArrayList<>());
        cJaccardPrecisionMap.put(ACCEPT, new ArrayList<>());
        cJaccardPrecisionMap.put(REJECT, new ArrayList<>()); 
        dJaccardPrecisionMap.put(ACCEPT, new ArrayList<>());
        dJaccardPrecisionMap.put(REJECT, new ArrayList<>());
        
        aJaroPrecisionMap.put(ACCEPT, new ArrayList<>());
        aJaroPrecisionMap.put(REJECT, new ArrayList<>());
        bJaroPrecisionMap.put(ACCEPT, new ArrayList<>());
        bJaroPrecisionMap.put(REJECT, new ArrayList<>());
        cJaroPrecisionMap.put(ACCEPT, new ArrayList<>());
        cJaroPrecisionMap.put(REJECT, new ArrayList<>()); 
        dJaroPrecisionMap.put(ACCEPT, new ArrayList<>());
        dJaroPrecisionMap.put(REJECT, new ArrayList<>()); 
        
        aJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        aJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>());
        bJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        bJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>());
        cJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        cJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>()); 
        dJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        dJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>()); 
        
        aSortedJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        aSortedJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>());
        bSortedJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        bSortedJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>());
        cSortedJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        cSortedJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>()); 
        dSortedJaroWinklerPrecisionMap.put(ACCEPT, new ArrayList<>());
        dSortedJaroWinklerPrecisionMap.put(REJECT, new ArrayList<>());         
    }

    /**
     * Executes the evaluation. 
     * 
     * @param csvPath the csv path containing the training samples.
     * @param resultsPath the output path of the results as a string.
     * @param propertyName the property name as a string.
     * @param thresholdsFilename the name of the optimal-threshold file.
     * @param notes the information of the current evaluation as a string. 
     * @throws FileNotFoundException indicates FileNotFoundException.
     * @throws IOException indicates I/O exception.
     */
    public void executeEvaluation(String csvPath, String resultsPath, String propertyName, String thresholdsFilename, 
            String notes) throws FileNotFoundException, IOException {

        String propertyPath = resultsPath + propertyName;
        File metricsFile = new File(propertyPath);

        if (metricsFile.exists()) {
            //clear contents
            PrintWriter pw = new PrintWriter(propertyPath);
            pw.close();
        } else {
            metricsFile.getParentFile().mkdirs();
            metricsFile.createNewFile();
        }
        
        LOG.info("Evaluation output at " + metricsFile.getAbsolutePath());

        try (BufferedWriter metricsWriter = new BufferedWriter(new FileWriter(metricsFile, true))) {
            
            double[] thresholds = 
                {0.05, 0.1, 0.15, 0.2, 0.30, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95};
            
            for (int i = 0; i < thresholds.length; i++) {

                clearPrecisionMapLists(aLevenPrecisionMap);
                clearPrecisionMapLists(bLevenPrecisionMap);
                clearPrecisionMapLists(cLevenPrecisionMap);
                clearPrecisionMapLists(dLevenPrecisionMap);
                
                clearPrecisionMapLists(aNGramPrecisionMap);
                clearPrecisionMapLists(bNGramPrecisionMap);
                clearPrecisionMapLists(cNGramPrecisionMap);
                clearPrecisionMapLists(dNGramPrecisionMap);

                clearPrecisionMapLists(aCosinePrecisionMap);
                clearPrecisionMapLists(bCosinePrecisionMap);
                clearPrecisionMapLists(cCosinePrecisionMap);
                clearPrecisionMapLists(dCosinePrecisionMap);

                clearPrecisionMapLists(aLqsPrecisionMap);
                clearPrecisionMapLists(bLqsPrecisionMap);
                clearPrecisionMapLists(cLqsPrecisionMap);
                clearPrecisionMapLists(dLqsPrecisionMap);

                clearPrecisionMapLists(aJaccardPrecisionMap);
                clearPrecisionMapLists(bJaccardPrecisionMap);
                clearPrecisionMapLists(cJaccardPrecisionMap);
                clearPrecisionMapLists(dJaccardPrecisionMap);

                clearPrecisionMapLists(aJaroPrecisionMap);
                clearPrecisionMapLists(bJaroPrecisionMap);
                clearPrecisionMapLists(cJaroPrecisionMap);
                clearPrecisionMapLists(dJaroPrecisionMap);

                clearPrecisionMapLists(aJaroWinklerPrecisionMap);
                clearPrecisionMapLists(bJaroWinklerPrecisionMap);
                clearPrecisionMapLists(cJaroWinklerPrecisionMap);
                clearPrecisionMapLists(dJaroWinklerPrecisionMap);

                clearPrecisionMapLists(aSortedJaroWinklerPrecisionMap);
                clearPrecisionMapLists(bSortedJaroWinklerPrecisionMap);
                clearPrecisionMapLists(cSortedJaroWinklerPrecisionMap);
                clearPrecisionMapLists(dSortedJaroWinklerPrecisionMap);
                
                double thres = thresholds[i];
                
                Accuracy aAccuracy = new Accuracy();
                Accuracy bAccuracy = new Accuracy();
                Accuracy cAccuracy = new Accuracy();
                Accuracy dAccuracy = new Accuracy();

                executeThreshold(metricsWriter, csvPath, thres, aAccuracy, bAccuracy, cAccuracy, dAccuracy);
            }
            
            String thresholdsPath = resultsPath + thresholdsFilename;
            File thresholdsFile = new File(thresholdsPath);

            if (thresholdsFile.exists()) {
                //clear contents
                PrintWriter pw = new PrintWriter(thresholdsPath);
                pw.close();
            } else {
                thresholdsFile.getParentFile().mkdirs();
                thresholdsFile.createNewFile();
            }
            
            try (BufferedWriter thresholdWriter = new BufferedWriter(new FileWriter(thresholdsFile, true))) {
                thresholdWriter.append(optimalThreshold.toString());
                thresholdWriter.newLine();
            }
            
            String notesPath = resultsPath + "notes.txt";
            File notesFile = new File(notesPath);

            if (notesFile.exists()) {
                //clear contents
                PrintWriter pw = new PrintWriter(notesPath);
                pw.close();
            } else {
                notesFile.getParentFile().mkdirs();
                notesFile.createNewFile();
            }

            try (BufferedWriter notesWriter = new BufferedWriter(new FileWriter(notesFile, true))) {
                notesWriter.append(notes);
                notesWriter.newLine();
            }
        }
    }

    private void executeThreshold(BufferedWriter writer, String csvPath, double threshold, 
            Accuracy aAccuracy, Accuracy bAccuracy, Accuracy cAccuracy, Accuracy dAccuracy) 
            throws FileNotFoundException, IOException{
        
        LOG.trace("Calculating for threshold " + threshold);
        
        totalRows = 0;
                
        String line;
        String cvsSplitBy = "\\^";
        Locale locale = configuration.getLocale();

        int l = 0;
        BufferedReader br = new BufferedReader(new FileReader(csvPath));

        try {
            
            //toggle configuration
            //executeOriginalSet(l, br, cvsSplitBy, locale, threshold, aAccuracy, bAccuracy, cAccuracy, dAccuracy);         
            executeBalancedSet(l, br, cvsSplitBy, locale, threshold, aAccuracy, bAccuracy, cAccuracy, dAccuracy);
            
            int aLevenPrecisionAcceptCounter = count(aLevenPrecisionMap.get(ACCEPT), threshold);
            int aLevenPrecisionRejectCounter = count(aLevenPrecisionMap.get(REJECT), threshold);
            int bLevenPrecisionAcceptCounter = count(bLevenPrecisionMap.get(ACCEPT), threshold);
            int bLevenPrecisionRejectCounter = count(bLevenPrecisionMap.get(REJECT), threshold);
            int cLevenPrecisionAcceptCounter = count(cLevenPrecisionMap.get(ACCEPT), threshold);
            int cLevenPrecisionRejectCounter = count(cLevenPrecisionMap.get(REJECT), threshold);
            int dLevenPrecisionAcceptCounter = count(dLevenPrecisionMap.get(ACCEPT), threshold);
            int dLevenPrecisionRejectCounter = count(dLevenPrecisionMap.get(REJECT), threshold);
            
            int aNGramPrecisionAcceptCounter = count(aNGramPrecisionMap.get(ACCEPT), threshold);
            int aNGramPrecisionRejectCounter = count(aNGramPrecisionMap.get(REJECT), threshold);
            int bNGramPrecisionAcceptCounter = count(bNGramPrecisionMap.get(ACCEPT), threshold);
            int bNGramPrecisionRejectCounter = count(bNGramPrecisionMap.get(REJECT), threshold);
            int cNGramPrecisionAcceptCounter = count(cNGramPrecisionMap.get(ACCEPT), threshold);
            int cNGramPrecisionRejectCounter = count(cNGramPrecisionMap.get(REJECT), threshold);
            int dNGramPrecisionAcceptCounter = count(dNGramPrecisionMap.get(ACCEPT), threshold);
            int dNGramPrecisionRejectCounter = count(dNGramPrecisionMap.get(REJECT), threshold);
            
            int aCosinePrecisionAcceptCounter = count(aCosinePrecisionMap.get(ACCEPT), threshold);
            int aCosinePrecisionRejectCounter = count(aCosinePrecisionMap.get(REJECT), threshold);
            int bCosinePrecisionAcceptCounter = count(bCosinePrecisionMap.get(ACCEPT), threshold);
            int bCosinePrecisionRejectCounter = count(bCosinePrecisionMap.get(REJECT), threshold);
            int cCosinePrecisionAcceptCounter = count(cCosinePrecisionMap.get(ACCEPT), threshold);
            int cCosinePrecisionRejectCounter = count(cCosinePrecisionMap.get(REJECT), threshold);
            int dCosinePrecisionAcceptCounter = count(dCosinePrecisionMap.get(ACCEPT), threshold);
            int dCosinePrecisionRejectCounter = count(dCosinePrecisionMap.get(REJECT), threshold);
            
            int aLqsPrecisionAcceptCounter = count(aLqsPrecisionMap.get(ACCEPT), threshold);
            int aLqsPrecisionRejectCounter = count(aLqsPrecisionMap.get(REJECT), threshold);
            int bLqsPrecisionAcceptCounter = count(bLqsPrecisionMap.get(ACCEPT), threshold);
            int bLqsPrecisionRejectCounter = count(bLqsPrecisionMap.get(REJECT), threshold);
            int cLqsPrecisionAcceptCounter = count(cLqsPrecisionMap.get(ACCEPT), threshold);
            int cLqsPrecisionRejectCounter = count(cLqsPrecisionMap.get(REJECT), threshold);
            int dLqsPrecisionAcceptCounter = count(dLqsPrecisionMap.get(ACCEPT), threshold);
            int dLqsPrecisionRejectCounter = count(dLqsPrecisionMap.get(REJECT), threshold);
            
            int aJaccardPrecisionAcceptCounter = count(aJaccardPrecisionMap.get(ACCEPT), threshold);
            int aJaccardPrecisionRejectCounter = count(aJaccardPrecisionMap.get(REJECT), threshold);
            int bJaccardPrecisionAcceptCounter = count(bJaccardPrecisionMap.get(ACCEPT), threshold);
            int bJaccardPrecisionRejectCounter = count(bJaccardPrecisionMap.get(REJECT), threshold);
            int cJaccardPrecisionAcceptCounter = count(cJaccardPrecisionMap.get(ACCEPT), threshold);
            int cJaccardPrecisionRejectCounter = count(cJaccardPrecisionMap.get(REJECT), threshold);
            int dJaccardPrecisionAcceptCounter = count(dJaccardPrecisionMap.get(ACCEPT), threshold);
            int dJaccardPrecisionRejectCounter = count(dJaccardPrecisionMap.get(REJECT), threshold);
            
            int aJaroPrecisionAcceptCounter = count(aJaroPrecisionMap.get(ACCEPT), threshold);
            int aJaroPrecisionRejectCounter = count(aJaroPrecisionMap.get(REJECT), threshold);
            int bJaroPrecisionAcceptCounter = count(bJaroPrecisionMap.get(ACCEPT), threshold);
            int bJaroPrecisionRejectCounter = count(bJaroPrecisionMap.get(REJECT), threshold);
            int cJaroPrecisionAcceptCounter = count(cJaroPrecisionMap.get(ACCEPT), threshold);
            int cJaroPrecisionRejectCounter = count(cJaroPrecisionMap.get(REJECT), threshold);
            int dJaroPrecisionAcceptCounter = count(dJaroPrecisionMap.get(ACCEPT), threshold);
            int dJaroPrecisionRejectCounter = count(dJaroPrecisionMap.get(REJECT), threshold);
            
            int aJaroWinklerPrecisionAcceptCounter = count(aJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int aJaroWinklerPrecisionRejectCounter = count(aJaroWinklerPrecisionMap.get(REJECT), threshold);
            int bJaroWinklerPrecisionAcceptCounter = count(bJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int bJaroWinklerPrecisionRejectCounter = count(bJaroWinklerPrecisionMap.get(REJECT), threshold);
            int cJaroWinklerPrecisionAcceptCounter = count(cJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int cJaroWinklerPrecisionRejectCounter = count(cJaroWinklerPrecisionMap.get(REJECT), threshold);
            int dJaroWinklerPrecisionAcceptCounter = count(dJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int dJaroWinklerPrecisionRejectCounter = count(dJaroWinklerPrecisionMap.get(REJECT), threshold);
            
            int aSortedJaroWinklerPrecisionAcceptCounter = count(aSortedJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int aSortedJaroWinklerPrecisionRejectCounter = count(aSortedJaroWinklerPrecisionMap.get(REJECT), threshold);
            int bSortedJaroWinklerPrecisionAcceptCounter = count(bSortedJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int bSortedJaroWinklerPrecisionRejectCounter = count(bSortedJaroWinklerPrecisionMap.get(REJECT), threshold);
            int cSortedJaroWinklerPrecisionAcceptCounter = count(cSortedJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int cSortedJaroWinklerPrecisionRejectCounter = count(cSortedJaroWinklerPrecisionMap.get(REJECT), threshold);
            int dSortedJaroWinklerPrecisionAcceptCounter = count(dSortedJaroWinklerPrecisionMap.get(ACCEPT), threshold);
            int dSortedJaroWinklerPrecisionRejectCounter = count(dSortedJaroWinklerPrecisionMap.get(REJECT), threshold);
            
            double aLevenAccuracy = calculateAccuracy(aAccuracy.getLevenshteinCount(), totalRows);
            double aLevenPrecision = calculatePrecision(aLevenPrecisionAcceptCounter, aLevenPrecisionRejectCounter);
            double aLevenRecall= calculateRecall(aLevenPrecisionAcceptCounter, aLevenPrecisionMap);
            double aLevenHarmonicMean = calculateHarmonicMean(aLevenPrecision, aLevenRecall);

            double bLevenAccuracy = calculateAccuracy(bAccuracy.getLevenshteinCount(), totalRows);
            double bLevenPrecision = calculatePrecision(bLevenPrecisionAcceptCounter, bLevenPrecisionRejectCounter);
            double bLevenRecall= calculateRecall(bLevenPrecisionAcceptCounter, bLevenPrecisionMap);
            double bLevenHarmonicMean = calculateHarmonicMean(bLevenPrecision, bLevenRecall);

            double cLevenAccuracy = calculateAccuracy(cAccuracy.getLevenshteinCount(), totalRows);
            double cLevenPrecision = calculatePrecision(cLevenPrecisionAcceptCounter, cLevenPrecisionRejectCounter);
            double cLevenRecall= calculateRecall(cLevenPrecisionAcceptCounter, cLevenPrecisionMap);
            double cLevenHarmonicMean = calculateHarmonicMean(cLevenPrecision, cLevenRecall);

            double dLevenAccuracy = calculateAccuracy(dAccuracy.getLevenshteinCount(), totalRows);
            double dLevenPrecision = calculatePrecision(dLevenPrecisionAcceptCounter, dLevenPrecisionRejectCounter);
            double dLevenRecall= calculateRecall(dLevenPrecisionAcceptCounter, dLevenPrecisionMap);
            double dLevenHarmonicMean = calculateHarmonicMean(dLevenPrecision, dLevenRecall);

            double aNGramAccuracy = calculateAccuracy(aAccuracy.getNgram2Count(), totalRows);
            double aNGramPrecision = calculatePrecision(aNGramPrecisionAcceptCounter, aNGramPrecisionRejectCounter);
            double aNGramRecall= calculateRecall(aNGramPrecisionAcceptCounter, aNGramPrecisionMap);
            double aNGramHarmonicMean = calculateHarmonicMean(aNGramPrecision, aNGramRecall);

            double bNGramAccuracy = calculateAccuracy(bAccuracy.getNgram2Count(), totalRows);
            double bNGramPrecision = calculatePrecision(bNGramPrecisionAcceptCounter, bNGramPrecisionRejectCounter);
            double bNGramRecall= calculateRecall(bNGramPrecisionAcceptCounter, bNGramPrecisionMap);
            double bNGramHarmonicMean = calculateHarmonicMean(bNGramPrecision, bNGramRecall);

            double cNGramAccuracy = calculateAccuracy(cAccuracy.getNgram2Count(), totalRows);
            double cNGramPrecision = calculatePrecision(cNGramPrecisionAcceptCounter, cNGramPrecisionRejectCounter);
            double cNGramRecall= calculateRecall(cNGramPrecisionAcceptCounter, cNGramPrecisionMap);
            double cNGramHarmonicMean = calculateHarmonicMean(cNGramPrecision, cNGramRecall);

            double dNGramAccuracy = calculateAccuracy(dAccuracy.getNgram2Count(), totalRows);
            double dNGramPrecision = calculatePrecision(dNGramPrecisionAcceptCounter, dNGramPrecisionRejectCounter);
            double dNGramRecall= calculateRecall(dNGramPrecisionAcceptCounter, dNGramPrecisionMap);
            double dNGramHarmonicMean = calculateHarmonicMean(dNGramPrecision, dNGramRecall);
            
            double aCosineAccuracy = calculateAccuracy(aAccuracy.getCosineCount(), totalRows);
            double aCosinePrecision = calculatePrecision(aCosinePrecisionAcceptCounter, aCosinePrecisionRejectCounter);
            double aCosineRecall= calculateRecall(aCosinePrecisionAcceptCounter, aCosinePrecisionMap);
            double aCosineHarmonicMean = calculateHarmonicMean(aCosinePrecision, aCosineRecall);

            double bCosineAccuracy = calculateAccuracy(bAccuracy.getCosineCount(), totalRows);
            double bCosinePrecision = calculatePrecision(bCosinePrecisionAcceptCounter, bCosinePrecisionRejectCounter);
            double bCosineRecall= calculateRecall(bCosinePrecisionAcceptCounter, bCosinePrecisionMap);
            double bCosineHarmonicMean = calculateHarmonicMean(bCosinePrecision, bCosineRecall);

            double cCosineAccuracy = calculateAccuracy(cAccuracy.getCosineCount(), totalRows);
            double cCosinePrecision = calculatePrecision(cCosinePrecisionAcceptCounter, cCosinePrecisionRejectCounter);
            double cCosineRecall= calculateRecall(cCosinePrecisionAcceptCounter, cCosinePrecisionMap);
            double cCosineHarmonicMean = calculateHarmonicMean(cCosinePrecision, cCosineRecall);

            double dCosineAccuracy = calculateAccuracy(dAccuracy.getCosineCount(), totalRows);
            double dCosinePrecision = calculatePrecision(dCosinePrecisionAcceptCounter, dCosinePrecisionRejectCounter);
            double dCosineRecall= calculateRecall(dCosinePrecisionAcceptCounter, dCosinePrecisionMap);
            double dCosineHarmonicMean = calculateHarmonicMean(dCosinePrecision, dCosineRecall);
            
            double aLqsAccuracy = calculateAccuracy(aAccuracy.getLqsCount(), totalRows);
            double aLqsPrecision = calculatePrecision(aLqsPrecisionAcceptCounter, aLqsPrecisionRejectCounter);
            double aLqsRecall= calculateRecall(aLqsPrecisionAcceptCounter, aLqsPrecisionMap);
            double aLqsHarmonicMean = calculateHarmonicMean(aLqsPrecision, aLqsRecall);

            double bLqsAccuracy = calculateAccuracy(bAccuracy.getLqsCount(), totalRows);
            double bLqsPrecision = calculatePrecision(bLqsPrecisionAcceptCounter, bLqsPrecisionRejectCounter);
            double bLqsRecall= calculateRecall(bLqsPrecisionAcceptCounter, bLqsPrecisionMap);
            double bLqsHarmonicMean = calculateHarmonicMean(bLqsPrecision, bLqsRecall);

            double cLqsAccuracy = calculateAccuracy(cAccuracy.getLqsCount(), totalRows);
            double cLqsPrecision = calculatePrecision(cLqsPrecisionAcceptCounter, cLqsPrecisionRejectCounter);
            double cLqsRecall= calculateRecall(cLqsPrecisionAcceptCounter, cLqsPrecisionMap);
            double cLqsHarmonicMean = calculateHarmonicMean(cLqsPrecision, cLqsRecall);
            
            double dLqsAccuracy = calculateAccuracy(dAccuracy.getLqsCount(), totalRows);
            double dLqsPrecision = calculatePrecision(dLqsPrecisionAcceptCounter, dLqsPrecisionRejectCounter);
            double dLqsRecall= calculateRecall(dLqsPrecisionAcceptCounter, dLqsPrecisionMap);
            double dLqsHarmonicMean = calculateHarmonicMean(dLqsPrecision, dLqsRecall);
            
            double aJaccardAccuracy = calculateAccuracy(aAccuracy.getJaccardCount(), totalRows);
            double aJaccardPrecision = calculatePrecision(aJaccardPrecisionAcceptCounter, aJaccardPrecisionRejectCounter);
            double aJaccardRecall= calculateRecall(aJaccardPrecisionAcceptCounter, aJaccardPrecisionMap);
            double aJaccardHarmonicMean = calculateHarmonicMean(aJaccardPrecision, aJaccardRecall);

            double bJaccardAccuracy = calculateAccuracy(bAccuracy.getJaccardCount(), totalRows);
            double bJaccardPrecision = calculatePrecision(bJaccardPrecisionAcceptCounter, bJaccardPrecisionRejectCounter);
            double bJaccardRecall= calculateRecall(bJaccardPrecisionAcceptCounter, bJaccardPrecisionMap);
            double bJaccardHarmonicMean = calculateHarmonicMean(bJaccardPrecision, bJaccardRecall);

            double cJaccardAccuracy = calculateAccuracy(cAccuracy.getJaccardCount(), totalRows);
            double cJaccardPrecision = calculatePrecision(cJaccardPrecisionAcceptCounter, cJaccardPrecisionRejectCounter);
            double cJaccardRecall= calculateRecall(cJaccardPrecisionAcceptCounter, cJaccardPrecisionMap);
            double cJaccardHarmonicMean = calculateHarmonicMean(cJaccardPrecision, cJaccardRecall);

            double dJaccardAccuracy = calculateAccuracy(dAccuracy.getJaccardCount(), totalRows);
            double dJaccardPrecision = calculatePrecision(dJaccardPrecisionAcceptCounter, dJaccardPrecisionRejectCounter);
            double dJaccardRecall= calculateRecall(dJaccardPrecisionAcceptCounter, dJaccardPrecisionMap);
            double dJaccardHarmonicMean = calculateHarmonicMean(dJaccardPrecision, dJaccardRecall);
            
            double aJaroAccuracy = calculateAccuracy(aAccuracy.getJaroCount(), totalRows);
            double aJaroPrecision = calculatePrecision(aJaroPrecisionAcceptCounter, aJaroPrecisionRejectCounter);
            double aJaroRecall= calculateRecall(aJaroPrecisionAcceptCounter, aJaroPrecisionMap);
            double aJaroHarmonicMean = calculateHarmonicMean(aJaroPrecision, aJaroRecall);

            double bJaroAccuracy = calculateAccuracy(bAccuracy.getJaroCount(), totalRows);
            double bJaroPrecision = calculatePrecision(bJaroPrecisionAcceptCounter, bJaroPrecisionRejectCounter);
            double bJaroRecall= calculateRecall(bJaroPrecisionAcceptCounter, bJaroPrecisionMap);
            double bJaroHarmonicMean = calculateHarmonicMean(bJaroPrecision, bJaroRecall);

            double cJaroAccuracy = calculateAccuracy(cAccuracy.getJaroCount(), totalRows);
            double cJaroPrecision = calculatePrecision(cJaroPrecisionAcceptCounter, cJaroPrecisionRejectCounter);
            double cJaroRecall= calculateRecall(cJaroPrecisionAcceptCounter, cJaroPrecisionMap);
            double cJaroHarmonicMean = calculateHarmonicMean(cJaroPrecision, cJaroRecall);

            double dJaroAccuracy = calculateAccuracy(dAccuracy.getJaroCount(), totalRows);
            double dJaroPrecision = calculatePrecision(dJaroPrecisionAcceptCounter, dJaroPrecisionRejectCounter);
            double dJaroRecall= calculateRecall(dJaroPrecisionAcceptCounter, dJaroPrecisionMap);
            double dJaroHarmonicMean = calculateHarmonicMean(dJaroPrecision, dJaroRecall);
            
            double aJaroWinklerAccuracy = calculateAccuracy(aAccuracy.getJaroWinklerCount(), totalRows);
            double aJaroWinklerPrecision = calculatePrecision(aJaroWinklerPrecisionAcceptCounter, aJaroWinklerPrecisionRejectCounter);
            double aJaroWinklerRecall= calculateRecall(aJaroWinklerPrecisionAcceptCounter, aJaroWinklerPrecisionMap);
            double aJaroWinklerHarmonicMean = calculateHarmonicMean(aJaroWinklerPrecision, aJaroWinklerRecall);

            double bJaroWinklerAccuracy = calculateAccuracy(bAccuracy.getJaroWinklerCount(), totalRows);
            double bJaroWinklerPrecision = calculatePrecision(bJaroWinklerPrecisionAcceptCounter, bJaroWinklerPrecisionRejectCounter);
            double bJaroWinklerRecall= calculateRecall(bJaroWinklerPrecisionAcceptCounter, bJaroWinklerPrecisionMap);
            double bJaroWinklerHarmonicMean = calculateHarmonicMean(bJaroWinklerPrecision, bJaroWinklerRecall);

            double cJaroWinklerAccuracy = calculateAccuracy(cAccuracy.getJaroWinklerCount(), totalRows);
            double cJaroWinklerPrecision = calculatePrecision(cJaroWinklerPrecisionAcceptCounter, cJaroWinklerPrecisionRejectCounter);
            double cJaroWinklerRecall= calculateRecall(cJaroWinklerPrecisionAcceptCounter, cJaroWinklerPrecisionMap);
            double cJaroWinklerHarmonicMean = calculateHarmonicMean(cJaroWinklerPrecision, cJaroWinklerRecall);

            double dJaroWinklerAccuracy = calculateAccuracy(dAccuracy.getJaroWinklerCount(), totalRows);
            double dJaroWinklerPrecision = calculatePrecision(dJaroWinklerPrecisionAcceptCounter, dJaroWinklerPrecisionRejectCounter);
            double dJaroWinklerRecall= calculateRecall(dJaroWinklerPrecisionAcceptCounter, dJaroWinklerPrecisionMap);
            double dJaroWinklerHarmonicMean = calculateHarmonicMean(dJaroWinklerPrecision, dJaroWinklerRecall);
            
            double aSortedJaroWinklerAccuracy = calculateAccuracy(aAccuracy.getJaroWinklerSortedCount(), totalRows);
            double aSortedJaroWinklerPrecision = calculatePrecision(aSortedJaroWinklerPrecisionAcceptCounter, aSortedJaroWinklerPrecisionRejectCounter);
            double aSortedJaroWinklerRecall= calculateRecall(aSortedJaroWinklerPrecisionAcceptCounter, aSortedJaroWinklerPrecisionMap);
            double aSortedJaroWinklerHarmonicMean = calculateHarmonicMean(aSortedJaroWinklerPrecision, aSortedJaroWinklerRecall);

            double bSortedJaroWinklerAccuracy = calculateAccuracy(bAccuracy.getJaroWinklerSortedCount(), totalRows);
            double bSortedJaroWinklerPrecision = calculatePrecision(bSortedJaroWinklerPrecisionAcceptCounter, bSortedJaroWinklerPrecisionRejectCounter);
            double bSortedJaroWinklerRecall= calculateRecall(bSortedJaroWinklerPrecisionAcceptCounter, bSortedJaroWinklerPrecisionMap);
            double bSortedJaroWinklerHarmonicMean = calculateHarmonicMean(bSortedJaroWinklerPrecision, bSortedJaroWinklerRecall);

            double cSortedJaroWinklerAccuracy = calculateAccuracy(cAccuracy.getJaroWinklerSortedCount(), totalRows);
            double cSortedJaroWinklerPrecision = calculatePrecision(cSortedJaroWinklerPrecisionAcceptCounter, cSortedJaroWinklerPrecisionRejectCounter);
            double cSortedJaroWinklerRecall= calculateRecall(cSortedJaroWinklerPrecisionAcceptCounter, cSortedJaroWinklerPrecisionMap);
            double cSortedJaroWinklerHarmonicMean = calculateHarmonicMean(cSortedJaroWinklerPrecision, cSortedJaroWinklerRecall);

            double dSortedJaroWinklerAccuracy = calculateAccuracy(dAccuracy.getJaroWinklerSortedCount(), totalRows);
            double dSortedJaroWinklerPrecision = calculatePrecision(dSortedJaroWinklerPrecisionAcceptCounter, dSortedJaroWinklerPrecisionRejectCounter);
            double dSortedJaroWinklerRecall= calculateRecall(dSortedJaroWinklerPrecisionAcceptCounter, dSortedJaroWinklerPrecisionMap);
            double dSortedJaroWinklerHarmonicMean = calculateHarmonicMean(dSortedJaroWinklerPrecision, dSortedJaroWinklerRecall);
            
            String aScores = scores("a", threshold, aLevenAccuracy, aLevenPrecision, aLevenRecall, aLevenHarmonicMean, 
                                            aNGramAccuracy, aNGramPrecision, aNGramRecall, aNGramHarmonicMean, 
                                            aCosineAccuracy, aCosinePrecision, aCosineRecall, aCosineHarmonicMean, 
                                            aLqsAccuracy, aLqsPrecision, aLqsRecall, aLqsHarmonicMean, aJaccardAccuracy, 
                                            aJaccardPrecision, aJaccardRecall, aJaccardHarmonicMean, aJaroAccuracy, 
                                            aJaroPrecision, aJaroRecall, aJaroHarmonicMean, aJaroWinklerAccuracy, 
                                            aJaroWinklerPrecision, aJaroWinklerRecall, aJaroWinklerHarmonicMean, 
                                            aSortedJaroWinklerAccuracy, aSortedJaroWinklerPrecision, 
                                            aSortedJaroWinklerRecall, aSortedJaroWinklerHarmonicMean);
            
            writer.append(aScores);
            writer.newLine();
            
            String bScores = scores("b", threshold, bLevenAccuracy, bLevenPrecision, bLevenRecall, 
                                    bLevenHarmonicMean, bNGramAccuracy, bNGramPrecision, bNGramRecall, 
                                    bNGramHarmonicMean, bCosineAccuracy, bCosinePrecision, bCosineRecall, 
                                    bCosineHarmonicMean, bLqsAccuracy, bLqsPrecision, bLqsRecall, bLqsHarmonicMean, 
                                    bJaccardAccuracy, bJaccardPrecision, bJaccardRecall, bJaccardHarmonicMean, 
                                    bJaroAccuracy, bJaroPrecision, bJaroRecall, bJaroHarmonicMean, bJaroWinklerAccuracy, 
                                    bJaroWinklerPrecision, bJaroWinklerRecall, bJaroWinklerHarmonicMean, 
                                    bSortedJaroWinklerAccuracy, bSortedJaroWinklerPrecision, bSortedJaroWinklerRecall, 
                                    bSortedJaroWinklerHarmonicMean);            

            writer.append(bScores);
            writer.newLine();

//exclude c scores
//            String cScores = scores("c", threshold, cLevenAccuracy, cLevenPrecision, cLevenRecall, cLevenHarmonicMean, 
//                            cNGramAccuracy, cNGramPrecision, cNGramRecall, cNGramHarmonicMean, cCosineAccuracy, 
//                            cCosinePrecision, cCosineRecall, cCosineHarmonicMean, cLqsAccuracy, cLqsPrecision, 
//                            cLqsRecall, cLqsHarmonicMean, cJaccardAccuracy, cJaccardPrecision, cJaccardRecall, 
//                            cJaccardHarmonicMean, cJaroAccuracy, cJaroPrecision, cJaroRecall, cJaroHarmonicMean, 
//                            cJaroWinklerAccuracy, cJaroWinklerPrecision, cJaroWinklerRecall, cJaroWinklerHarmonicMean, 
//                            cSortedJaroWinklerAccuracy, cSortedJaroWinklerPrecision, cSortedJaroWinklerRecall, 
//                            cSortedJaroWinklerHarmonicMean);            
//
//
//            
//            writer.append(cScores);
//            writer.newLine();
            
            String dScores = scores("d", threshold, dLevenAccuracy, dLevenPrecision, dLevenRecall, dLevenHarmonicMean, 
                            dNGramAccuracy, dNGramPrecision, dNGramRecall, dNGramHarmonicMean, dCosineAccuracy, 
                            dCosinePrecision, dCosineRecall, dCosineHarmonicMean, dLqsAccuracy, dLqsPrecision, 
                            dLqsRecall, dLqsHarmonicMean, dJaccardAccuracy, dJaccardPrecision, dJaccardRecall, 
                            dJaccardHarmonicMean, dJaroAccuracy, dJaroPrecision, dJaroRecall, dJaroHarmonicMean, 
                            dJaroWinklerAccuracy, dJaroWinklerPrecision, dJaroWinklerRecall, dJaroWinklerHarmonicMean, 
                            dSortedJaroWinklerAccuracy, dSortedJaroWinklerPrecision, dSortedJaroWinklerRecall, 
                            dSortedJaroWinklerHarmonicMean);   
            
            writer.append(dScores);
            writer.newLine();            
            writer.newLine();
            writer.newLine();
            writer.newLine();
            
        } catch(IOException | RuntimeException ex){
            
            writer.close();
            throw new ApplicationException(ex.getMessage());
        }
                
    }

    private void executeOriginalSet(int l, BufferedReader br, String cvsSplitBy, Locale locale, double threshold, 
            Accuracy aAccuracy, Accuracy bAccuracy, Accuracy cAccuracy, Accuracy dAccuracy) throws IOException {
        String line;
        l = 0;
        while ((line = br.readLine()) != null) {
            
            //skip first line of csv
            if (l < 1) {
                l++;
                continue;
            }
            
            // use comma as separator
            String[] spl = line.split(cvsSplitBy);
            
            //StringBuffer sb = new StringBuffer("");
            if (spl.length < 22) {
                continue;
            }
            String idA = spl[0];
            String idB = spl[1];
            
            //String distanceMeters = spl[2];
            
            String nameA = spl[3];
            String nameB = spl[4];
            //String nameFusionAction = spl[5];
            
//            String streetA = spl[6];
//            String streetB = spl[7];
//            //String streetFusionAction = spl[8];
//            
//            String streetNumberA = spl[9];
//            String streetNumberB = spl[10];
//            
//            String phoneA = spl[11];
//            String phoneB = spl[12];
//            //String phoneFusionAction = spl[13];
//            
//            String emailA = spl[14];
//            String emailB = spl[15];
//            //String emailFusionAction = spl[16];
//            
//            String websiteA = spl[17];
//            String websiteB = spl[18];
            //String websiteFusionAction = spl[19];
            
            //String score = spl[20];
            //String names1 = spl[21];
            String acceptance = spl[22];
            
            computePairOutputResult(nameA, nameB, locale, acceptance, threshold,
                    aAccuracy, bAccuracy, cAccuracy, dAccuracy);
            
            l++;
        }
    }

    private void executeBalancedSet(int l, BufferedReader br, String cvsSplitBy, Locale locale, double threshold, 
            Accuracy aAccuracy, Accuracy bAccuracy, Accuracy cAccuracy, Accuracy dAccuracy) throws IOException {
    
        List<Record> records = new ArrayList<>();
        String line;
        l = 0;

        while ((line = br.readLine()) != null) {
            //skip first line of csv
            if (l < 1) {
                l++;
                continue;
            }
            
            // use comma as separator
            String[] spl = line.split(cvsSplitBy);
            
            //StringBuffer sb = new StringBuffer("");
            if (spl.length < 22) {
                continue;
            }

            String nameA = spl[3];
            String nameB = spl[4];
            String acceptance = spl[22];
            
            Record record = new Record();
            record.setKey(l);
            record.setNameA(nameA);
            record.setNameB(nameB);
            record.setAcceptance(acceptance);
            
            records.add(record);
            
            l++;
        }
        
        
        //balance the record list
        int acceptCount = getAcceptCount(records);
        int rejectCount = getRejectCount(records);
        
        List<Record> balanced = new ArrayList<>();
        List<Record> accepts = new ArrayList<>();
        List<Record> rejects = new ArrayList<>();
        long seed = 123456;
        
        if(acceptCount > rejectCount){
            int diff = acceptCount - rejectCount;
            
            for(Record record : records){
                if(record.getAcceptance().equals(ACCEPT)){
                    accepts.add(record);
                } else if(record.getAcceptance().equals(REJECT)){
                    rejects.add(record);
                } else {
                    throw new ApplicationException("Wrong acceptance value: " + record.getAcceptance());
                }
            }
            
            Collections.shuffle(accepts, new Random(seed));
            
            accepts = accepts.subList(diff, accepts.size());
            
            balanced.addAll(accepts);
            balanced.addAll(rejects);
            
        } else if(acceptCount < rejectCount){
            int diff = rejectCount - acceptCount;
            
            for(Record record : records){
                if(record.getAcceptance().equals(ACCEPT)){
                    accepts.add(record);
                } else if(record.getAcceptance().equals(REJECT)){
                    rejects.add(record);
                } else {
                    throw new ApplicationException("Wrong acceptance value: " + record.getAcceptance());
                }
            }
            

            Collections.shuffle(rejects, new Random(seed));
            
            rejects = rejects.subList(diff, rejects.size());
            
            balanced.addAll(accepts);
            balanced.addAll(rejects);            
            
        }
        
        //compute using balanced
        for (Record record : balanced) {
            
            computePairOutputResult(record.getNameA(), record.getNameB(), locale, record.getAcceptance(), threshold,
                    aAccuracy, bAccuracy, cAccuracy, dAccuracy);
            
            l++;
        }
    }
    
    private int getAcceptCount(List<Record> records){
        int count = 0;
        for(Record record : records){
            if(record.getAcceptance().equals(ACCEPT)){
                count++;
            }
        }
        return count;
    }

    private int getRejectCount(List<Record> records){
        int count = 0;
        for(Record record : records){
            if(record.getAcceptance().equals(REJECT)){
                count++;
            }
        }
        return count;
    }
    
    private String scores(String ind, double threshold, double aLevenAccuracy, double aLevenPrecision, double aLevenRecall, 
        double aLevenHarmonicMean, double aNGramAccuracy, double aNGramPrecision, double aNGramRecall, 
        double aNGramHarmonicMean, double aCosineAccuracy, double aCosinePrecision, double aCosineRecall, 
        double aCosineHarmonicMean, double aLqsAccuracy, double aLqsPrecision, double aLqsRecall, 
        double aLqsHarmonicMean, double aJaccardAccuracy, double aJaccardPrecision, double aJaccardRecall, 
        double aJaccardHarmonicMean, double aJaroAccuracy, double aJaroPrecision, double aJaroRecall, 
        double aJaroHarmonicMean, double aJaroWinklerAccuracy, double aJaroWinklerPrecision, 
        double aJaroWinklerRecall, double aJaroWinklerHarmonicMean, double aSortedJaroWinklerAccuracy, 
        double aSortedJaroWinklerPrecision, double aSortedJaroWinklerRecall, double aSortedJaroWinklerHarmonicMean) {

        String scores = "Total: " + totalRows
                +  "\nMetric_Threshold " + threshold + SEP + "Accuracy" + SEP +"Precision"+ SEP + "Recall" + SEP + "harmonicMean"
                + " \nLevenstein_" + ind + threshold + SEP + aLevenAccuracy + SEP + aLevenPrecision +  SEP + aLevenRecall + SEP + aLevenHarmonicMean
                + " \n2Gram_" + ind + threshold + SEP + aNGramAccuracy + SEP + aNGramPrecision +  SEP + aNGramRecall + SEP + aNGramHarmonicMean
                //+ " \nCosine_" + ind + threshold + SEP + aCosineAccuracy + SEP + aCosinePrecision +  SEP + aCosineRecall + SEP + aCosineHarmonicMean
                + " \nLongestCommonSubseq_" + ind + threshold + SEP + aLqsAccuracy + SEP + aLqsPrecision +  SEP + aLqsRecall + SEP + aLqsHarmonicMean
                //+ " \nJaccard_" + ind + threshold + SEP + aJaccardAccuracy + SEP + aJaccardPrecision +  SEP + aJaccardRecall + SEP + aJaccardHarmonicMean
                + " \nJaro_" + ind + threshold + SEP + aJaroAccuracy + SEP + aJaroPrecision +  SEP + aJaroRecall + SEP + aJaroHarmonicMean
                + " \nJaroWinkler_" + ind + threshold + SEP + aJaroWinklerAccuracy + SEP + aJaroWinklerPrecision +  SEP + aJaroWinklerRecall + SEP + aJaroWinklerHarmonicMean;
                //+ " \nSortedJaroWinkler_" + ind + threshold + SEP + aSortedJaroWinklerAccuracy + SEP + aSortedJaroWinklerPrecision +  SEP + aSortedJaroWinklerRecall + SEP + aSortedJaroWinklerHarmonicMean;
        return scores;
    }

    private void computePairOutputResult(String valueA, String valueB, 
            Locale locale, String acceptance, double threshold, Accuracy aAccuracy, 
            Accuracy bAccuracy, Accuracy cAccuracy, Accuracy dAccuracy) {
        
        totalRows++;
        
        NormalizedLiteral basicA = getBasicNormalization(valueA, valueB, locale);
        NormalizedLiteral basicB = getBasicNormalization(valueB, valueA, locale);
        
        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);
        
        String basicNormA = basicA.getNormalized();
        String basicNormB = basicB.getNormalized();
        
        double aLeven = Levenshtein.computeSimilarity(valueA, valueB, null);
        double aNGram = NGram.computeSimilarity(valueA, valueB, 2);
        double aCosine = Cosine.computeSimilarity(valueA, valueB);
        double aLqs = LongestCommonSubsequenceMetric.computeSimilarity(valueA, valueB);
        double aJaccard = Jaccard.computeSimilarity(valueA, valueB);
        double aJaro = Jaro.computeSimilarity(valueA, valueB);
        double aJaroWinkler = JaroWinkler.computeSimilarity(valueA, valueB);
        double aSortedJaroWinkler = SortedJaroWinkler.computeSimilarity(valueA, valueB);

        constructPrecisionMap(aLevenPrecisionMap, acceptance, aLeven);
        constructPrecisionMap(aNGramPrecisionMap, acceptance, aNGram);
        constructPrecisionMap(aCosinePrecisionMap, acceptance, aCosine);
        constructPrecisionMap(aLqsPrecisionMap, acceptance, aLqs);
        constructPrecisionMap(aJaccardPrecisionMap, acceptance, aJaccard);
        constructPrecisionMap(aJaroPrecisionMap, acceptance, aJaro);
        constructPrecisionMap(aJaroWinklerPrecisionMap, acceptance, aJaroWinkler);
        constructPrecisionMap(aSortedJaroWinklerPrecisionMap, acceptance, aSortedJaroWinkler);

        double bLeven = Levenshtein.computeSimilarity(basicNormA, basicNormB, null);
        double bNGram = NGram.computeSimilarity(basicNormA, basicNormB, 2);
        double bCosine = Cosine.computeSimilarity(basicNormA, basicNormB);
        double bLqs = LongestCommonSubsequenceMetric.computeSimilarity(basicNormA, basicNormB);
        double bJaccard = Jaccard.computeSimilarity(basicNormA, basicNormB);
        double bJaro = Jaro.computeSimilarity(basicNormA, basicNormB);
        double bJaroWinkler = JaroWinkler.computeSimilarity(basicNormA, basicNormB);
        double bSortedJaroWinkler = SortedJaroWinkler.computeSimilarity(basicNormA, basicNormB);
        
        constructPrecisionMap(bLevenPrecisionMap, acceptance, bLeven);
        constructPrecisionMap(bNGramPrecisionMap, acceptance, bNGram);
        constructPrecisionMap(bCosinePrecisionMap, acceptance, bCosine);
        constructPrecisionMap(bLqsPrecisionMap, acceptance, bLqs);
        constructPrecisionMap(bJaccardPrecisionMap, acceptance, bJaccard);
        constructPrecisionMap(bJaroPrecisionMap, acceptance, bJaro);
        constructPrecisionMap(bJaroWinklerPrecisionMap, acceptance, bJaroWinkler);
        constructPrecisionMap(bSortedJaroWinklerPrecisionMap, acceptance, bSortedJaroWinkler);
                
        double cLeven = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.LEVENSHTEIN);
        double cNGram = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.GRAM_2);
        double cCosine = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.COSINE);
        double cLqs = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.LCS);
        double cJaccard = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.JACCARD);
        double cJaro = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO);
        double cJaroWinkler = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO_WINKLER);
        double cSortedJaroWinkler = WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.SORTED_JARO_WINKLER);
        
        constructPrecisionMap(cLevenPrecisionMap, acceptance, cLeven);
        constructPrecisionMap(cNGramPrecisionMap, acceptance, cNGram);
        constructPrecisionMap(cCosinePrecisionMap, acceptance, cCosine);
        constructPrecisionMap(cLqsPrecisionMap, acceptance, cLqs);
        constructPrecisionMap(cJaccardPrecisionMap, acceptance, cJaccard);
        constructPrecisionMap(cJaroPrecisionMap, acceptance, cJaro);
        constructPrecisionMap(cJaroWinklerPrecisionMap, acceptance, cJaroWinkler);
        constructPrecisionMap(cSortedJaroWinklerPrecisionMap, acceptance, cSortedJaroWinkler);

        double dLeven = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.LEVENSHTEIN);
        double dNGram = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.GRAM_2);
        double dCosine = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.COSINE);
        double dLqs = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.LCS);
        double dJaccard = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.JACCARD);
        double dJaro = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO);
        double dJaroWinkler = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO_WINKLER);
        double dSortedJaroWinkler = WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.SORTED_JARO_WINKLER);
        
        constructPrecisionMap(dLevenPrecisionMap, acceptance, dLeven);
        constructPrecisionMap(dNGramPrecisionMap, acceptance, dNGram);
        constructPrecisionMap(dCosinePrecisionMap, acceptance, dCosine);
        constructPrecisionMap(dLqsPrecisionMap, acceptance, dLqs);
        constructPrecisionMap(dJaccardPrecisionMap, acceptance, dJaccard);
        constructPrecisionMap(dJaroPrecisionMap, acceptance, dJaro);
        constructPrecisionMap(dJaroWinklerPrecisionMap, acceptance, dJaroWinkler);
        constructPrecisionMap(dSortedJaroWinklerPrecisionMap, acceptance, dSortedJaroWinkler);

        //a
        if((aLeven >= threshold && acceptance.equals(ACCEPT)) || aLeven < threshold && acceptance.equals(REJECT)){
            aAccuracy.setLevenshteinCount(aAccuracy.getLevenshteinCount() + 1);
        }

        if((aNGram >= threshold && acceptance.equals(ACCEPT)) || aNGram < threshold && acceptance.equals(REJECT)){
            aAccuracy.setNgram2Count(aAccuracy.getNgram2Count() + 1);
        } 

        if((aCosine >= threshold && acceptance.equals(ACCEPT)) || aCosine < threshold && acceptance.equals(REJECT)){
            aAccuracy.setCosineCount(aAccuracy.getCosineCount() + 1);
        } 

        if((aLqs >= threshold && acceptance.equals(ACCEPT)) || aLqs < threshold && acceptance.equals(REJECT)){
            aAccuracy.setLqsCount(aAccuracy.getLqsCount() + 1);
        } 

        if((aJaccard >= threshold && acceptance.equals(ACCEPT)) || aJaccard < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaccardCount(aAccuracy.getJaccardCount() + 1);
        }
        
        if((aJaro >= threshold && acceptance.equals(ACCEPT)) || aJaro < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaroCount(aAccuracy.getJaroCount() + 1);
        } 

        if((aJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || aJaroWinkler < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaroWinklerCount(aAccuracy.getJaroWinklerCount() + 1);
        } 

        if((aSortedJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || aSortedJaroWinkler < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaroWinklerSortedCount(aAccuracy.getJaroWinklerSortedCount() + 1);
        } 


        //b norm

        if((bLeven >= threshold && acceptance.equals(ACCEPT)) || bLeven < threshold && acceptance.equals(REJECT)){
            bAccuracy.setLevenshteinCount(bAccuracy.getLevenshteinCount() + 1);
        }

        if((bNGram >= threshold && acceptance.equals(ACCEPT)) || bNGram < threshold && acceptance.equals(REJECT)){           
            bAccuracy.setNgram2Count(bAccuracy.getNgram2Count() + 1);
        } 

        if((bCosine >= threshold && acceptance.equals(ACCEPT)) || bCosine < threshold && acceptance.equals(REJECT)){
            bAccuracy.setCosineCount(bAccuracy.getCosineCount() + 1);
        } 

        if((bLqs >= threshold && acceptance.equals(ACCEPT)) || bLqs < threshold && acceptance.equals(REJECT)){
            bAccuracy.setLqsCount(bAccuracy.getLqsCount() + 1);
        } 
        
        if((bJaccard >= threshold && acceptance.equals(ACCEPT)) || bJaccard < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaccardCount(bAccuracy.getJaccardCount() + 1);
        }
        
        if((bJaro >= threshold && acceptance.equals(ACCEPT)) || bJaro < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaroCount(bAccuracy.getJaroCount() + 1);
        } 

        if((bJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || bJaroWinkler < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaroWinklerCount(bAccuracy.getJaroWinklerCount() + 1);
        } 

        if((bSortedJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || bSortedJaroWinkler < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaroWinklerSortedCount(bAccuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        
        //c norm
        
        if((cLeven >= threshold && acceptance.equals(ACCEPT)) || cLeven < threshold && acceptance.equals(REJECT)){
            cAccuracy.setLevenshteinCount(cAccuracy.getLevenshteinCount() + 1);
        }

        if((cNGram >= threshold && acceptance.equals(ACCEPT)) || cNGram < threshold && acceptance.equals(REJECT)){
            cAccuracy.setNgram2Count(cAccuracy.getNgram2Count() + 1);
        } 

        if((cCosine >= threshold && acceptance.equals(ACCEPT)) || cCosine < threshold && acceptance.equals(REJECT)){
            cAccuracy.setCosineCount(cAccuracy.getCosineCount() + 1);
        } 

        if((cLqs >= threshold && acceptance.equals(ACCEPT)) || cLqs < threshold && acceptance.equals(REJECT)){
            cAccuracy.setLqsCount(cAccuracy.getLqsCount() + 1);
        } 
        
        if((cJaccard >= threshold && acceptance.equals(ACCEPT)) || cJaccard < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaccardCount(cAccuracy.getJaccardCount() + 1);
        }
        
        if((cJaro >= threshold && acceptance.equals(ACCEPT)) || cJaro < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaroCount(cAccuracy.getJaroCount() + 1);
        } 

        if((cJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || cJaroWinkler < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaroWinklerCount(cAccuracy.getJaroWinklerCount() + 1);
        } 

        if((cSortedJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || cSortedJaroWinkler < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaroWinklerSortedCount(cAccuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        //d norm
        
        if((dLeven >= threshold && acceptance.equals(ACCEPT)) || dLeven < threshold && acceptance.equals(REJECT)){
            dAccuracy.setLevenshteinCount(dAccuracy.getLevenshteinCount() + 1);
        }

        if((dNGram >= threshold && acceptance.equals(ACCEPT)) || dNGram < threshold && acceptance.equals(REJECT)){
            dAccuracy.setNgram2Count(dAccuracy.getNgram2Count() + 1);
        } 

        if((dCosine >= threshold && acceptance.equals(ACCEPT)) || dCosine < threshold && acceptance.equals(REJECT)){
            dAccuracy.setCosineCount(dAccuracy.getCosineCount() + 1);
        } 

        if((dLqs >= threshold && acceptance.equals(ACCEPT)) || dLqs < threshold && acceptance.equals(REJECT)){
            dAccuracy.setLqsCount(dAccuracy.getLqsCount() + 1);
        } 
        
        if((dJaccard >= threshold && acceptance.equals(ACCEPT)) || dJaccard < threshold && acceptance.equals(REJECT)){
            dAccuracy.setJaccardCount(dAccuracy.getJaccardCount() + 1);
        }
        
        if((dJaro >= threshold && acceptance.equals(ACCEPT)) || dJaro < threshold && acceptance.equals(REJECT)){
            dAccuracy.setJaroCount(dAccuracy.getJaroCount() + 1);
        } 

        if((dJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || dJaroWinkler < threshold && acceptance.equals(REJECT)){
            dAccuracy.setJaroWinklerCount(dAccuracy.getJaroWinklerCount() + 1);
        } 

        if((dSortedJaroWinkler >= threshold && acceptance.equals(ACCEPT)) || dSortedJaroWinkler < threshold && acceptance.equals(REJECT)){
            dAccuracy.setJaroWinklerSortedCount(dAccuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        //find best accuracy in order to set optimal threshold
        int maxAccuracyLeven = getMaxAccuracy(aAccuracy.getLevenshteinCount(), 
                bAccuracy.getLevenshteinCount(), cAccuracy.getLevenshteinCount(), dAccuracy.getLevenshteinCount());
        
        int maxAccuracyNGram = getMaxAccuracy(aAccuracy.getNgram2Count(), 
                    bAccuracy.getNgram2Count(), cAccuracy.getNgram2Count(), dAccuracy.getNgram2Count());
        
        int maxAccuracyCosine = getMaxAccuracy(aAccuracy.getCosineCount(), 
                bAccuracy.getCosineCount(), cAccuracy.getCosineCount(), dAccuracy.getCosineCount());

        int maxAccuracyLqs = getMaxAccuracy(aAccuracy.getLqsCount(), 
                bAccuracy.getLqsCount(), cAccuracy.getLqsCount(), dAccuracy.getLqsCount());

        int maxAccuracyJaccard = getMaxAccuracy(aAccuracy.getJaccardCount(), 
                bAccuracy.getJaccardCount(), cAccuracy.getJaccardCount(), dAccuracy.getJaccardCount());

        int maxAccuracyJaro = getMaxAccuracy(aAccuracy.getJaroCount(), 
                bAccuracy.getJaroCount(), cAccuracy.getJaroCount(), dAccuracy.getJaroCount());

        int maxAccuracyJaroWinkler = getMaxAccuracy(aAccuracy.getJaroWinklerCount(), 
                bAccuracy.getJaroWinklerCount(), cAccuracy.getJaroWinklerCount(), dAccuracy.getJaroWinklerCount());

        int maxAccuracySortedJaroWinkler = getMaxAccuracy(aAccuracy.getJaroWinklerSortedCount(), 
                bAccuracy.getJaroWinklerSortedCount(), cAccuracy.getJaroWinklerSortedCount(), dAccuracy.getJaroWinklerSortedCount());
        
        if(maxAccuracyLeven > optimalAccuracy.getLevenshteinCount()){
            optimalAccuracy.setLevenshteinCount(maxAccuracyLeven);
            optimalThreshold.setLevenshtein(threshold);
        }
        
        if(maxAccuracyNGram > optimalAccuracy.getNgram2Count()){
            optimalAccuracy.setNgram2Count(maxAccuracyNGram);
            optimalThreshold.setNgram2(threshold);
        }
        
        if(maxAccuracyCosine > optimalAccuracy.getCosineCount()){
            optimalAccuracy.setCosineCount(maxAccuracyCosine);
            optimalThreshold.setCosine(threshold);
        } 
        
        if(maxAccuracyLqs > optimalAccuracy.getLqsCount()){
            optimalAccuracy.setLqsCount(maxAccuracyLqs);
            optimalThreshold.setLqs(threshold);
        }  
        
        if(maxAccuracyJaccard > optimalAccuracy.getJaccardCount()){
            optimalAccuracy.setJaccardCount(maxAccuracyJaccard);
            optimalThreshold.setJaccard(threshold);
        }
        
        if(maxAccuracyJaro > optimalAccuracy.getJaroCount()){
            optimalAccuracy.setJaroCount(maxAccuracyJaro);
            optimalThreshold.setJaro(threshold);
        }  
        
        if(maxAccuracyJaroWinkler > optimalAccuracy.getJaroWinklerCount()){
            optimalAccuracy.setJaroWinklerCount(maxAccuracyJaroWinkler);
            optimalThreshold.setJaroWinkler(threshold);
        } 
        
        if(maxAccuracySortedJaroWinkler > optimalAccuracy.getJaroWinklerSortedCount()){
            optimalAccuracy.setJaroWinklerSortedCount(maxAccuracySortedJaroWinkler);
            optimalThreshold.setSortedJaroWinkler(threshold);
        }         
    }

    private NormalizedLiteral getBasicNormalization(String literalA, String literalB, Locale locale) {
        BasicGenericNormalizer bgn = new BasicGenericNormalizer();
        NormalizedLiteral normalizedLiteral = bgn.getNormalizedLiteral(literalA, literalB, locale);
        return normalizedLiteral;
    }

    private WeightedPairLiteral getAdvancedNormalization(NormalizedLiteral normA, NormalizedLiteral normB, Locale locale) {
        AdvancedGenericNormalizer agn = new AdvancedGenericNormalizer();
        WeightedPairLiteral weightedPairLiteral = agn.getWeightedPair(normA, normB, locale);
        return weightedPairLiteral;
    }

    private void constructPrecisionMap(Map<String, List<Double>> precisionMap, String acceptance, double similarity) {
        if(acceptance.equals(ACCEPT)){
            precisionMap.get(ACCEPT).add(similarity);
        } else if(acceptance.equals(REJECT)){
            precisionMap.get(REJECT).add(similarity);
        } else {
            throw new ApplicationException("Wrong acceptance value: " + acceptance);
        }
    }

    private void clearPrecisionMapLists(Map<String, List<Double>> precisionMap){
        precisionMap.get(ACCEPT).clear();
        precisionMap.get(REJECT).clear();
    }
    
    private double calculateAccuracy(int count, int rows) {
        if(rows == 0){
            LOG.warn("Could not calculate accuracy. No rows found.");
            return 0;
        } else {
            return roundHalfUp(count / (double)rows);
        } 
    }
    
    private double calculatePrecision(int acceptCounter, int rejectCounter) {
        double result;
        
        if(acceptCounter == 0 && rejectCounter == 0){
            result = 0;
        } else {
            result = roundHalfUp(acceptCounter / (double)(acceptCounter + rejectCounter));
        }
        return result;
    }

    private double calculateRecall(int acceptCounter, Map<String, List<Double>> precisionMap) {
        double result;
        
        if(acceptCounter == 0){
            result = 0;
        } else {
            result = roundHalfUp(acceptCounter / (double) precisionMap.get(ACCEPT).size());
        }
        
        return result;

    }

    private double calculateHarmonicMean(double precision, double recall) {
        double result;
        
        if(precision == 0 || recall == 0){
            result = 0;
        } else {
            result = roundHalfUp(2 * precision * recall / (double)(precision + recall));
        }
        
        return result;
    }
    
    private double roundHalfUp(double d){
        return new BigDecimal(d).setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_3, RoundingMode.HALF_UP).doubleValue();     
    }
    
    private int count(List<Double> accepted, double threshold) {
        int counter = 0;
        for(Double elem : accepted){
            if(elem >= threshold){
                counter++;
            }
        }
        return counter;
    }

    private int getMaxAccuracy(int aAccuracy, int bAccuracy, int cAccuracy, int dAccuracy) {
        return  Math.max(Math.max(Math.max(aAccuracy,bAccuracy),cAccuracy), dAccuracy);
    }
}
