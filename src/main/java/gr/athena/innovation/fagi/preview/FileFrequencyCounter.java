package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.normalizer.SimpleLiteralNormalizer;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates frequencies of words extracted from the literal of a given property. Expects RDF N-triples but treats the
 * input as plain text.
 *
 * @author nkarag
 */
public class FileFrequencyCounter implements FrequencyCounter {

    private static final Logger logger = LogManager.getLogger(FileFrequencyCounter.class);

    private Locale locale;
    private List<String> properties;
    private final FusionSpecification fusionSpecification;
    private final int frequentTopK;

    public FileFrequencyCounter(FusionSpecification fusionSpecification, int frequentTopK) {
        this.fusionSpecification = fusionSpecification;
        this.frequentTopK = frequentTopK;
    }

    @Override
    public void export(String inputFilename) {

        int index = 0;
        for (String property : properties) {

            StringBuilder prop = new StringBuilder(property);

            if (!property.startsWith("<")) {
                prop.insert(0, "<");
                prop.insert(prop.length(), ">");
            }

            File propertyFile = new File(fusionSpecification.getPathOutput());
            File parentDir = propertyFile.getParentFile();
            String filename;

            if (property.lastIndexOf("#") != -1) {
                filename = property.substring(property.lastIndexOf("#") + 1);
            } else if (property.lastIndexOf("/") != -1) {
                filename = property.substring(property.lastIndexOf("/") + 1);
            } else {
                filename = "_" + index;
            }

            String outputFilename = parentDir.getPath() + "/frequencies/" + filename + ".freq.txt";
            File outputFile = new File(outputFilename);

            PrintWriter pw = null;
            try {

                if (outputFile.exists()) {
                    //clear contents
                    pw = new PrintWriter(outputFile);
                    pw.close();

                } else {
                    outputFile.getParentFile().mkdirs();
                    outputFile.createNewFile();
                }

                writePropertyFrequency(prop, inputFilename, outputFilename);

            } catch (FileNotFoundException ex) {
                throw new ApplicationException(ex.getMessage());
            } catch (IOException ex) {
                throw new ApplicationException(ex.getMessage());
            } finally {
                if(pw != null){
                    pw.close();
                }
            }
        }
    }

    private void writePropertyFrequency(StringBuilder property, String inputFilename, String outputFilename) throws IOException {
        BufferedWriter writer = null;
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilename));
            writer = new BufferedWriter(new FileWriter(outputFilename, true));
            String line;
            String splitBy = "\\s+";

            Frequency frequency = new Frequency();

            while ((line = bufferedReader.readLine()) != null) {

                String[] spl = line.split(splitBy);

                if (spl[1].contentEquals(property)) {
                    String[] tokens = Arrays.copyOfRange(spl, 2, spl.length);

                    String literal = String.join(" ", tokens);

                    SimpleLiteralNormalizer normalizer = new SimpleLiteralNormalizer();
                    String bNorm = normalizer.normalize(literal, locale);
                    String[] toks = tokenize(bNorm);
                    
                    frequency.insert(toks);
                }
            }

            Map<String, Integer> frequencyMap = frequency.getTopKFrequency(frequentTopK);

            //title with the name of the property
            writer.append("# " + property);
            writer.newLine();
            for (String key : frequencyMap.keySet()) {
                String value = frequencyMap.get(key).toString();
                String pair = key + "=" + value;
                writer.append(pair);
                writer.newLine();
            }

            writer.close();

        } catch (IOException | RuntimeException ex) {
            if (writer != null) {
                writer.close();
            }
            logger.error(ex);
            throw new ApplicationException(ex.getMessage());
        }
    }

    //tokenize on whitespaces
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");
        String[] split = text.toString().split("\\s+");
        return split;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}