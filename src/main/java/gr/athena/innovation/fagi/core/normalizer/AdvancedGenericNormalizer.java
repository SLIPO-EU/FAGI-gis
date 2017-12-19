package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.model.EnumEntity;
import gr.athena.innovation.fagi.model.LinkedTerm;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class AdvancedGenericNormalizer {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AdvancedGenericNormalizer.class);
    
    private static final char CONNECTOR = SpecificationConstants.CONNECTOR;
    
    private final double baseWeight = 0.5;
    private final double linkedWeight = 0.1;
    private final double mismatchWeight = 0.4;

    /**
     *
     * @param normalizedLiteralA
     * @param normalizedLiteralB
     * @param locale
     * @return
     */
    public WeightedPairLiteral getWeightedPair(NormalizedLiteral normalizedLiteralA, 
            NormalizedLiteral normalizedLiteralB, Locale locale) {

        WeightedPairLiteral weightedPairLiteral = new WeightedPairLiteral();

        String normalizedA = normalizedLiteralA.getNormalized();
        String normalizedB = normalizedLiteralB.getNormalized();

        List<String> tokensA = getTokenList(normalizedA);
        List<String> tokensB = getTokenList(normalizedB);

        Set<String> setA = new HashSet<>(tokensA);
        Set<String> setB = new HashSet<>(tokensB);

        Set<String> terms = TermResolver.getInstance().getTerms();

        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();

        resolveTerms(weightedPairLiteral, setA, setB, terms, tokensA, EnumEntity.LEFT);
        resolveTerms(weightedPairLiteral, setB, setA, terms, tokensB, EnumEntity.RIGHT);

        appendTokens(tokensA, a);
        appendTokens(tokensB, b);
        
        String baseA = a.toString();
        String baseB = b.toString();

        weightedPairLiteral.setBaseWeight(0.5);

        //custom alphabetical re-ordering, assign mismatches
        WeightedPairLiteral weightedPair = assignMismatch(weightedPairLiteral, tokenize(baseA), tokenize(baseB), locale);
        
        return weightedPair;
    }

    private WeightedPairLiteral assignMismatch(WeightedPairLiteral weightedPairLiteral, String[] tokensAar, 
            String[] tokensBar, Locale locale) {

        Collator collator = resolveCollator(locale);

        List<String> mismatchA = new ArrayList<>();
        List<String> mismatchB = new ArrayList<>();
        
        List<String> tokensA = new LinkedList<>(Arrays.asList(tokensAar));
        List<String> tokensB = new LinkedList<>(Arrays.asList(tokensBar));
        
        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();

        int carret_i = 0;
        int carret_j = 0;

        int br = 0;
        
        while(br < tokensA.size() + tokensB.size()) {

            String ta = tokensA.get(carret_i);
            String tb = tokensB.get(carret_j);
            
            int compareResult = collator.compare(ta, tb);

            if (compareResult == 0) {

                a.append(ta).append(CONNECTOR);
                b.append(tb).append(CONNECTOR);

                carret_i++;
                carret_j++;

                if (carret_j > tokensA.size() - 1) {

                    appendOffSets(carret_j, b, tokensB);
                    
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);

                } else if (carret_i > tokensB.size() - 1) {
                    
                    appendOffSets(carret_i, a, tokensA);
                    
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
                }
            } else if (compareResult > 0) {

                mismatchB.add(tb);

                carret_j++;

                if (carret_j > tokensA.size() - 1) {
                    
                    appendOffSets(carret_j, b, tokensB);
                    
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
                }

            } else {

                mismatchA.add(ta);
                
                carret_i++;

                if (carret_i > tokensB.size() - 1) {
                    
                    appendOffSets(carret_i, a, tokensA);
                    
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
                }
            }
            br++;
        }

        return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
    }

    private Collator resolveCollator(Locale locale) {
        if(locale == null){
            locale = Locale.ENGLISH;
        }
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(SpecificationConstants.COLLATOR_STRENGTH);
        
        return collator;
    }

    private List<String> getTokenList(String text) {
        List<String> tokens = new LinkedList<>(Arrays.asList(tokenize(text)));
        return tokens;
    }

    private WeightedPairLiteral getWeightedPairLiteral(WeightedPairLiteral weightedPairLiteral,
            List<String> mismatchA, List<String> mismatchB, StringBuilder a, StringBuilder b) {

        weightedPairLiteral.setMismatchTokensA(mismatchA);
        weightedPairLiteral.setMismatchTokensB(mismatchB);
        weightedPairLiteral.setBaseValueA(a.toString().trim());
        weightedPairLiteral.setBaseValueB(b.toString().trim());

        return weightedPairLiteral;
    }

    //tokenize on whitespaces
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }
    
    private void addLinkedTerm(WeightedPairLiteral weightedPairLiteral, List<String> tokens, String token) {
        LinkedTerm linkedTerm = new LinkedTerm();
        linkedTerm.setTerm(token);
        linkedTerm.setWeight(linkedWeight);

        tokens.remove(token);

        weightedPairLiteral.addLinkedTerm(linkedTerm);
    }

    private void appendTokens(List<String> tokens, StringBuilder builder) {
        tokens.stream().forEach((tok) -> {
            builder.append(tok).append(" ");
        });        
    }

    //identify special/frequent terms:
    //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
    //-If only one contains them -> exclude it and assign a small weight for the mismatch
    private void resolveTerms(WeightedPairLiteral weightedPairLiteral, 
            Set<String> set, Set<String> helpSet, Set<String> terms, List<String> tokens, EnumEntity entity){
        
        set.stream().forEach((token) -> {
            if (terms.contains(token) && helpSet.contains(token)) {
                addLinkedTerm(weightedPairLiteral, tokens, token);
            } else if (terms.contains(token) && !helpSet.contains(token)) {
                tokens.remove(token);
                
                switch(entity) {
                    case LEFT:
                        weightedPairLiteral.addUniqueSpecialTermA(token);
                        break;
                    case RIGHT:
                        weightedPairLiteral.addUniqueSpecialTermB(token);
                        break;
                }
            }
        });
    }
    
    private void appendOffSets(int carret, StringBuilder builder, List<String> tokens) {
        while(carret < tokens.size()-1){
            builder.append(tokens.get(carret)).append(CONNECTOR);
            carret++;
        }
    }
}