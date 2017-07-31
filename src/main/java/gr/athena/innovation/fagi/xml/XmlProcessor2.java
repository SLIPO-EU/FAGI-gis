package gr.athena.innovation.fagi.xml;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;
import gr.athena.innovation.fagi.core.rule.ActionRule;
import gr.athena.innovation.fagi.core.rule.ConditionTag;
import gr.athena.innovation.fagi.core.rule.Expression;
import gr.athena.innovation.fagi.core.rule.ExpressionTag;
import gr.athena.innovation.fagi.core.rule.LogicalExpressionTag;
import gr.athena.innovation.fagi.core.rule.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import static gr.athena.innovation.fagi.core.specification.SpecificationConstants.AND;
import static gr.athena.innovation.fagi.core.specification.SpecificationConstants.OR;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.ENTITY_NODE;
import static org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
import static org.w3c.dom.Node.NOTATION_NODE;
import static org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 *
 *  Reads the XML file that contains the fusion rules 
 *  and turns it into in-memory structures that the rest of the application can access.
 *  This processor is does not validate the XML input. 
 *  The validation is performed one step before using the {@link gr.athena.innovation.fagi.core.rule.XmlValidator}
 * 
 * @author nkarag
 */
public class XmlProcessor2 {

    private static final Logger logger = LogManager.getLogger(XmlProcessor2.class);
    private final RuleCatalog ruleCatalog;
    private int actionRuleCount = 1;
    private int steps = 0;
    
    /**
     *
     * @param ruleCatalog
     */
    public XmlProcessor2(RuleCatalog ruleCatalog){
        this.ruleCatalog = ruleCatalog;
    }


    /**
     *
     *  The rules are parsed and modeled in memory as follows:
          Rule
            |
        ActionRule 
            |
        Condition
            |
        Expression

        Its node may have multiple children
     * 
     * @param path
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void parseRules(String path) throws ParserConfigurationException, SAXException, IOException{

        logger.info("Reading specification from path: " + path);

        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        
        //get all <RULE> elements of the XML. The rule elements are all in the same level
        NodeList nList = doc.getElementsByTagName("RULE");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            logger.info("rule " + temp);
            Rule rule = new Rule();
            Node ruleNode = nList.item(temp);
            NodeList ruleNodeList = ruleNode.getChildNodes();
            createRule(ruleNodeList, rule);
            //ruleCatalog.addItem(constructRule(ruleChilds.item(k)));

        }
        System.exit(0);
    }

    /*
        Parse propertyA, propertyB and ACTION_RULE_SET of the current rule
    */
    private void createRule(NodeList ruleNodeList, Rule rule){
        int length = ruleNodeList.getLength();
        for (int i = 0; i < length; i++) {
            //logger.info("rule iter " + i);
            short type = ruleNodeList.item(i).getNodeType();
            //logger.debug("CREATE RULE, TYPE: " + nodeType(type));
            
            if (ruleNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element ruleElement = (Element) ruleNodeList.item(i);
                if (ruleElement.getNodeName().contains("PROPERTYA")) {
                    logger.debug("property A: " + ruleElement.getTextContent());
                    rule.setPropertyA(ruleElement.getTextContent());

                } else if (ruleElement.getNodeName().contains("PROPERTYB")) {
                    logger.debug("property B: " + ruleElement.getTextContent());
                    rule.setPropertyB(ruleElement.getTextContent());
                } else if(ruleElement.getNodeName().contains("DEFAULT_GEO_ACTION")){
                    rule.setDefaultMetaAction(EnumMetadataActions.fromString(ruleElement.getTextContent()));
                } else if(ruleElement.getNodeName().contains("DEFAULT_META_ACTION")){
                    rule.setDefaultMetaAction(EnumMetadataActions.fromString(ruleElement.getTextContent()));
                } else if(ruleElement.getNodeName().contains("ACTION_RULE_SET")){
                    logger.debug("found rules set, count: " + ruleElement.getFirstChild().getNodeType());
                    NodeList actionRuleNodeList = ruleElement.getElementsByTagName("ACTION_RULE");
                    createActionRules(actionRuleNodeList, rule);
                }
            }
        }        
    }
    
    /*
        Parse each action rule from the action rule set. All action rules are on the same level
    */
    private void createActionRules(NodeList actionRuleNodeList, Rule rule){
        //logger.info("~~~~~~~ Action Rules ~~~~~~~");
        
        int length = actionRuleNodeList.getLength();
        for (int i = 0; i < length; i++) {

            Node actionRuleNode = actionRuleNodeList.item(i);

            if (actionRuleNode.getNodeType() == Node.ELEMENT_NODE) {
                ActionRule actionRule = new ActionRule();
                Element actionRuleElement = (Element) actionRuleNode;
                
                createActionRule(actionRuleElement, actionRule);
            }
        }          
    }
    
    private void createActionRule(Element actionRuleElement, ActionRule actionRule){
        logger.fatal("new ACTION RULE " + actionRuleCount);
        actionRuleCount++;
        //NodeList actionRuleChilds = actionRuleElement.getChildNodes();
        
        //Extract ACTION element and its text inside ACTION_RULE
        Node action = actionRuleElement.getLastChild();

        int i = 0;
        while(!action.getNodeName().equalsIgnoreCase("ACTION")){
            action = action.getPreviousSibling();
            i++;
            if(i>5000){
                //TODO - remove this check when xsd validation is complete
                logger.fatal("Could not find \"ACTION\" tag inside \"ACTION_RULE\". Check the XML input.");
                throw new RuntimeException();
            }
        }

        EnumGeometricActions geoAction = EnumGeometricActions.fromString(action.getTextContent());
        if(geoAction.equals(EnumGeometricActions.UNDEFINED)){
            EnumMetadataActions metaAction = EnumMetadataActions.fromString(action.getTextContent());
            if(metaAction.equals(EnumMetadataActions.UNDEFINED)){
                logger.fatal("Wrong action input: " + action.getTextContent());
                //TODO - remove exception when the xsd validation is complete.
                throw new RuntimeException();
            } else {
                actionRule.setMetaAction(metaAction);
            }
        } else {
            actionRule.setGeoAction(geoAction);
        }

        NodeList conditionsList = actionRuleElement.getElementsByTagName("CONDITION");
        if(conditionsList.getLength() != 1){
            //TODO - remove this check after xsd validation is complete
            logger.fatal("Found more than one condition inside ACTION_RULE. Please check the XML input file.");
            throw new RuntimeException();
        }

        //logger.fatal("CLASS " + conditionsList.item(0).getClass());

        Node conditionNode = conditionsList.item(0);
        
        
        
        ConditionTag con = constructCondition(conditionNode);
        
        logger.trace("FINAL CONDITION: " + con.toString());
        

    }
    
    private ConditionTag constructCondition(Node conditionNode) {
        ConditionTag conditionTag = new ConditionTag();
        
        //if it is simple, construct simple functions
        //else extract Expression, passing the Expression Child node.
        
        
        Node parentExpression = conditionNode.getFirstChild();
        while(parentExpression != null){
            if(parentExpression.getNodeType() == Node.ELEMENT_NODE){
                if(parentExpression.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    break;
                }
            }  
            parentExpression = parentExpression.getNextSibling();
        }
        

        if(parentExpressionContainsSingleFunction(parentExpression)){
            //The Condition is simple. Contains only a function.
            ExpressionTag et = new ExpressionTag();
            et.setExpression(getSingleFunction(parentExpression));
            conditionTag.setExpressionTag(et);
            return conditionTag;
        }
        

        //The condition does not contain a single function.
        
        //1st call of the recursive function with 0 depth.
        String logicalOperationType = getLogicalOperationType(parentExpression);
        extractExpression(conditionTag, parentExpression, logicalOperationType, 0);
        
        return conditionTag;
    } 
    
    //recursive method
    private void extractExpression(ConditionTag conditionTag, Node expression, String type, int depth) {
        steps++;
        logger.warn("diving.. depth: " + depth);
//        logger.warn("current expression is: " + expression.getNodeName());
//        logger.warn("current expression text: " + expression.getTextContent());
        if(depth>6){
            return;
        }
        
        
        
        //expression can exist in three forms"
        //contains only functions
        //contains only other expressions
        //contains expression and function

        if(containsOnlyFunctionChilds(expression)){
            logger.trace("case 1, depth " + depth);
            //1. Contains only functions under a logical operation. 
            //Each level can have ONLY ONE logical operation. 
            //The operation however can have more than one expressions or functions.

            //Find which logical operation exists in this level. There should be exactly one.

            //if all first level elements are function elements, then stop recursing this branch.            
            
            LogicalExpressionTag logicalExpression = new LogicalExpressionTag(type, depth);

            
            
            List<ExpressionTag> simpleFunctions = getSimpleFunctionsOfLogicalOperation(expression);
            //System.out.println("@@@ logical created! depth " + depth + " type " + type + " simpleFunctions " + simpleFunctions);
            
            logicalExpression.setExpressionTags(simpleFunctions);
            conditionTag.setExpressionTag(logicalExpression);
            conditionTag.addNode(logicalExpression);
            
            //logger.info(conditionTag.toString());
        } else if(containsExpressionAndFunctionChilds(expression)){
            logger.trace("case 2, depth " + depth);
            //logger.trace("found both expression and function");
            
            //2. The expression contains at least one expression and one function. 
            //String logicalOperationType = getLogicalOperationType(expression);
            
            List<ExpressionTag> simpleFunctions = getSimpleFunctionsOfLogicalOperation(expression);
            //System.out.println("@@@ logical created! depth " + depth + " type " + type + " simpleFunctions " + simpleFunctions);
            
            LogicalExpressionTag logicalExpression = new LogicalExpressionTag(type, depth);
            logicalExpression.setExpressionTags(simpleFunctions);
            conditionTag.setExpressionTag(logicalExpression);
            conditionTag.addNode(logicalExpression);            
            
            List<Node> expressions = getLogicalExpressionChildNodes(expression);

            String parentLogicalType = getLogicalOperationType(expression);
            LogicalExpressionTag parentLogicalExpression = new LogicalExpressionTag(parentLogicalType, depth);
            parentLogicalExpression.setKey("parent " + steps);
            conditionTag.addNode(parentLogicalExpression);
            for(Node childExpression : expressions){

                String childLogicalType = getLogicalOperationType(childExpression);
                //Call extractExpression for each expression.
                extractExpression(conditionTag, childExpression, childLogicalType, depth+1);

            }
            //logger.error("expression name: " + expression.getNodeName());
            //logger.error("logical type: " + logicalOperationType);
            //logger.error("parent logical type: " + type);
            //extractExpression(conditionTag, expression, logicalOperationType, depth+1);
            
            //count expressionTag childs and create additional expressionTags to put into list
            //count Expressions and create additional LogicalExpressionTags to put into list
            
            //conditionTag.setExpressionTag(lo);
            
        } else if(containsOnlyExpressionChilds(expression)){
            logger.trace("case 3, depth " + depth);
            //contains two or more expressions under a logical operation.

            List<Node> expressions = getLogicalExpressionChildNodes(expression);

            String parentLogicalType = getLogicalOperationType(expression);
            LogicalExpressionTag parentLogicalExpression = new LogicalExpressionTag(parentLogicalType, depth);
            parentLogicalExpression.setKey("parent " + steps);
            conditionTag.addNode(parentLogicalExpression);
            for(Node childExpression : expressions){

                String childLogicalType = getLogicalOperationType(childExpression);
                //Call extractExpression for each expression.
                extractExpression(conditionTag, childExpression, childLogicalType, depth+1);

            }
        }
    }  

    private boolean containsOnlyFunctionChilds(Node expression) {

        boolean hasOnlyFunctions = false;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();

        while(child != null){
            
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                    hasOnlyFunctions = true;
                } else {
                    return false;
                }
            }
            child = child.getNextSibling();
        }
        return hasOnlyFunctions;
    }
    
    private boolean containsExpressionAndFunctionChilds(Node expression) {
        boolean containsFunction = false;
        boolean containsExpression = false;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();

        while(child != null){
            
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                    containsFunction = true;
                } else if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    containsExpression = true;
                }
            }
            child = child.getNextSibling();
        }
        return containsFunction && containsExpression;
    }
    
    private boolean containsOnlyExpressionChilds(Node expression) {

        boolean hasOnlyExpressions = false;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();

        while(child != null){
            
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    hasOnlyExpressions = true;
                } else {
                    return false;
                }
            }
            child = child.getNextSibling();
        }
        return hasOnlyExpressions;
    }    

    private boolean parentExpressionContainsSingleFunction(Node parentExpression) {
        if(parentExpression.getNodeType() == Node.ELEMENT_NODE){
            Element parentExpressionElement = (Element) parentExpression;
            NodeList functions = parentExpressionElement.getElementsByTagName(SpecificationConstants.FUNCTION);
            return functions.getLength() == 1;
        }
        return false;
    }

    private String getSingleFunction(Node parentExpression) {
        Node child = parentExpression.getFirstChild();
        while(child !=null){
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if (child.getNodeName().equals(SpecificationConstants.FUNCTION)){
                    return child.getTextContent();
                }
            }
            child = child.getNextSibling();
        }
        return null;
    }

    private String getLogicalOperationType(Node parentExpression) {
        Node child = parentExpression.getFirstChild();
        while(child != null){
            if(child.getNodeType() == Node.ELEMENT_NODE){
                String name = child.getNodeName();
                switch(name){
                    case AND:
                        return "AND";
                    case OR:
                        return "OR";
                    default:
                        logger.fatal("Expression in XML does not contain a logical operation! " + child.getNodeName());
                        throw new RuntimeException();
                }
            }
            child = child.getNextSibling();
        }
        
        logger.fatal("Expression in XML does not contain a logical operation! ", parentExpression);
        throw new RuntimeException();
    }

    //this method returns a list with all functions under a logical operation. 
    //The input is the parent node of the logical operation (Expression node)
    private List<ExpressionTag> getSimpleFunctionsOfLogicalOperation(Node expression) {
        List<ExpressionTag> list = new ArrayList<>();

        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                ExpressionTag et = new ExpressionTag();
                et.setExpression(child.getTextContent());
                list.add(et);
            }
            child = child.getNextSibling();
        }
        return list;
    }

    //this method returns a list with all expressions under a logical operation. 
    //The input is the parent node of the logical operation (Expression node)
    private List<LogicalExpressionTag> getExpressionsOfLogicalOperation(ConditionTag conditionTag, Node expression, int depth) {
        List<LogicalExpressionTag> list = new ArrayList<>();

        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                String type = getLogicalOperationType(child);
                LogicalExpressionTag et = new LogicalExpressionTag(type, depth);
                extractExpression(conditionTag, expression, type, depth);
                conditionTag.setExpressionTag(et);
                list.add(et);
            }
            child = child.getNextSibling();
        }
        return list;
    }

    private List<LogicalExpressionTag> getExpressionsAndFunctionsOfLogicalOperation(ConditionTag conditionTag, Node expression, int depth) {
        List<LogicalExpressionTag> list = new ArrayList<>();

        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                //found function. Set the simple function string on LogicalExpression (inherited field).
                String type = getLogicalOperationType(child);
                LogicalExpressionTag et = new LogicalExpressionTag(type, depth);
                et.setExpression(child.getTextContent());
                list.add(et);
            } else if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                
                String logicalOperationType = getLogicalOperationType(expression);
                extractExpression(conditionTag, expression, logicalOperationType, depth+1);                

//                LogicalExpressionTag et = new LogicalExpressionTag(type, depth);
//                et.setExpression(child.getTextContent());
//                list.add(et);                
            }
            child = child.getNextSibling();
        }
        return list;
    }
    
    private Node getLogicalOperationNode(Node expression){
        Node logicalOperationNode = expression.getFirstChild(); //first level child is the logical operation of the expression
        
        //get the logical operation node. Should always exist:
        int i=0;
        while(true){
            
            if(logicalOperationNode.getNodeType() == Node.ELEMENT_NODE){
                if(logicalOperationNode.getNodeName().equalsIgnoreCase(AND) || logicalOperationNode.getNodeName().equalsIgnoreCase(OR) ){
                    break;
                } 
            }
            
            if(i>5000){ //erroneous xml input check
                logger.fatal("Expression in XML does not contain a logical operation! ", logicalOperationNode);
                throw new RuntimeException();
            }

            i++;
            logicalOperationNode = logicalOperationNode.getNextSibling();
        }
        
        return logicalOperationNode;
    }

    private int countExpressionsUnderLogicalOperation(Node expression) {
        int count = 0;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                count++;

            }
            child = child.getNextSibling();
        }
        return count;
    }

    private List<Node> getLogicalExpressionChildNodes(Node expression) {
        List<Node> childExpressions = new ArrayList<>();
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node childExpression = logicalOperationNode.getFirstChild();
        while(childExpression != null){
            if(childExpression.getNodeType() == Node.ELEMENT_NODE){
                if(childExpression.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    childExpressions.add(childExpression);
                }
            }
            childExpression = childExpression.getNextSibling();
        }
        return childExpressions;
    }

    private static String nodeType(short type) {
        
        switch(type) {
            case ELEMENT_NODE:                return "Element";
            case DOCUMENT_TYPE_NODE:          return "Document type";
            case ENTITY_NODE:                 return "Entity";
            case ENTITY_REFERENCE_NODE:       return "Entity reference";
            case NOTATION_NODE:               return "Notation";
            case TEXT_NODE:                   return "Text";
            case COMMENT_NODE:                return "Comment";
            case CDATA_SECTION_NODE:          return "CDATA Section";
            case ATTRIBUTE_NODE:              return "Attribute";
            case PROCESSING_INSTRUCTION_NODE: return "Attribute";
        }
        return "Unidentified";
    }  

}