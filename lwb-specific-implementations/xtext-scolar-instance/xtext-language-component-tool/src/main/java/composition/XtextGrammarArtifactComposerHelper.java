package composition;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.*;
import org.eclipse.xtext.resource.XtextResourceSet;
import util.Binding;

/**
 * Composes two xtext grammars to a new one.
 *
 * @author Jerome Pfeiffer
 */
public class XtextGrammarArtifactComposerHelper {

    private MCPath modelPath;

    public Map<String, Grammar> getLoadedGrammarsCache() {
        return loadedGrammarsCache;
    }

    /**
     * Stores intermediate products during grammar composition in-memory. This
     * is required for the composition of nested components (e.g., for realizing nested features
     * in a language family)
     */
    private Map<String, Grammar> loadedGrammarsCache = new HashMap<>();
    private String lastComposedGrammarName = "";

    private Optional<Grammar> result = Optional.empty();

    private XtextFactory factory;

    XtextResourceSet resourceSet = null;

    /**
     * Constructor for composition.MCGrammarArtifactComposer
     *
     * @param modelPath Model path to use when resolving MontiCore grammars
     */
    public XtextGrammarArtifactComposerHelper(MCPath modelPath, XtextResourceSet resourceSet) {
        this.modelPath = modelPath;
        factory = XtextFactory.eINSTANCE;
        this.resourceSet = resourceSet;
    }




    public Optional<Grammar> getResult() {
       return this.result;
    }


    /**
     * Loads the grammar with the given name.<br>
     * Cached grammars have precedence over grammars on the
     * model path.
     *
     * @param grammarName Qualified name of the grammar to load
     * @return The loaded grammar, if found.
     */
    public Grammar loadGrammar(ASTMCQualifiedName grammarName) {
        Resource res = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI(grammarName.getBaseName() + ".xtext"));
        try {
            String qName = grammarName.getQName();
            String qFolderName = qName.replace(".", "/");
            for (Path p : modelPath.getEntries()) {
                res.load(new FileInputStream(p.toFile().toString() + "/" + qFolderName + ".xtext"), null);
                if (res.isLoaded()) {
                    return (Grammar) res.getContents().get(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    // Utility method to find a rule by name (e.g., ID from Terminals)
    private static AbstractRule findRuleInImports(Grammar grammar, String name) {
        for (Grammar g : grammar.getUsedGrammars()) {
            for (AbstractRule rule : g.getRules()) {
                if (rule.getName().equals(name)) return rule;
            }
        }
        return null;
    }

    /**
     * Determine the rule with the given name in the given grammar
     *
     * @param grammar The grammar to check
     * @param name    The name of the rule to search for
     * @return The found rule, if present
     */
    private static AbstractRule findRuleWithNameInGrammar(Grammar grammar, String name) {
        for (AbstractRule rule : grammar.getRules()) {
            if (rule.getName().equals(name)) return rule;
        }
        return null;
    }





    public Grammar composeGrammars(Grammar providedGrammar, Grammar requiringGrammar, ASTLanguageComponent peComponent, ASTLanguageComponent reComponent, Collection<Binding> bindings) {
        // 1. Create new composed xtext grammar
        Grammar composedGrammar = createNewGrammar(providedGrammar.getName(), requiringGrammar.getName());

        // 2. Extend target grammar
        if (lastComposedGrammarName.isEmpty()) {
            composedGrammar.getUsedGrammars().add(requiringGrammar);
        }
        else {
            composedGrammar.getUsedGrammars().add(loadedGrammarsCache.get(lastComposedGrammarName));
        }

        Map<AbstractRule, AbstractRule> originalRule2Copies = new HashMap<>();

        // 3. Import Ecore and add rules of source grammar.
        addEcoreAndAddRules(providedGrammar, composedGrammar);


        // 4. add all bound rules as alternatives
        for (Binding binding : bindings) {
            String providedExtension = binding.getProvisionPoint();
            Optional<ASTProvidedExtension> providedExtensionPoint = peComponent.getProvisionPoint(providedExtension);
            String requiredExtension = binding.getExtensionPoint();
            Optional<ASTRequiredExtension> requiredExtensionPoint = reComponent.getExtensionPoint(requiredExtension);

            //4. Overwrite and add the target rule of the target grammar and add to the composed grammar.
            if (requiredExtensionPoint.isPresent() && providedExtensionPoint.isPresent()) {
                String referencedRequiredRule = requiredExtensionPoint.get().isPresentReferencedRule() ? requiredExtensionPoint.get().getReferencedRule() : requiredExtensionPoint.get().getName();
                String referencedProvidedRule = providedExtensionPoint.get().isPresentReferencedRule() ? providedExtensionPoint.get().getReferencedRule() : providedExtensionPoint.get().getName();

                boolean foundRule = false;
                // TODO this breaks when the referencedRequiredRule comes from a transitively referenced rule
                for (AbstractRule originalRule : requiringGrammar.getRules()) {
                    if(originalRule.getName().equals(referencedRequiredRule)) {
                        ParserRule ruleOverwrite = createRuleOverwrite((ParserRule) originalRule);
                        AbstractRule sourceRuleLocally = findRuleWithNameInGrammar(composedGrammar, referencedProvidedRule);
                        ruleOverwrite = addAlternative(ruleOverwrite, (ParserRule) sourceRuleLocally);
                        originalRule2Copies.put(originalRule, ruleOverwrite);
                        composedGrammar.getRules().add(ruleOverwrite);
                        foundRule = true;
                        break;
                    }
                }
            }
            else {
                Log.error("Provided or required extension are not available for " + requiredExtension + " and " + providedExtension);
            }
        }
        redirectRuleCallsToLocalCopies(composedGrammar, originalRule2Copies);

        this.result = Optional.of(composedGrammar);

        return composedGrammar;
    }


    private Grammar createNewGrammar(String fqnSourceGrammarName, String fqnTargetGrammarName) {
        String[] partsSourceName = fqnSourceGrammarName.split("\\.");
        String simpleSourceName = partsSourceName[partsSourceName.length - 1];

//        String[] partsTargetName = fqnTargetGrammarName.split("\\.");
//        String simpleTargetName = partsTargetName[partsTargetName.length - 1];

        Grammar result = factory.createGrammar();
        result.setName(fqnTargetGrammarName + "With" + simpleSourceName);

        return result;
    }

    private ParserRule createRuleOverwrite(ParserRule sourceRule) {
        if (!sourceRule.getAnnotations().stream().filter(a -> a.getName().equals("Override")).findAny().isPresent()) {
            ParserRule overrideRule = EcoreUtil.copy(sourceRule);;
            Annotation overrideAnnotation = factory.createAnnotation();
            overrideAnnotation.setName("Override");
            overrideRule.getAnnotations().add(overrideAnnotation);
            return overrideRule;
        }
        return sourceRule;

    }


    private void addEcoreAndAddRules(Grammar source, Grammar target) {

        GeneratedMetamodel abstractMetamodelDeclaration = ((GeneratedMetamodel) source.getMetamodelDeclarations().stream().filter(md -> md instanceof GeneratedMetamodel).findAny().get());
        String alias = abstractMetamodelDeclaration.getName();

        ReferencedMetamodel referencedMetamodel = factory.createReferencedMetamodel();
        referencedMetamodel.setAlias(alias);
        referencedMetamodel.setEPackage(abstractMetamodelDeclaration.getEPackage());
        target.getMetamodelDeclarations().add(referencedMetamodel);

        Map<AbstractRule, AbstractRule> original2copied = new HashMap<>();

        for(AbstractRule rule: source.getRules()) {
            AbstractRule ruleToAdd = EcoreUtil.copy(rule);
            TypeRef typeRef = factory.createTypeRef();
            typeRef.setMetamodel(referencedMetamodel);
            typeRef.setClassifier(referencedMetamodel.getEPackage().getEClassifier(ruleToAdd.getName()));
            ruleToAdd.setType(typeRef);
            target.getRules().add(ruleToAdd); // Use copy to avoid containment issues
            original2copied.put(rule, ruleToAdd);
        }
        redirectRuleCallsToLocalCopies(target, original2copied);
    }

    private ParserRule addAlternative(ParserRule toExtend, ParserRule newAlternative) {

        Alternatives alternatives;
        if (toExtend.getAlternatives() instanceof Alternatives) {
            alternatives = (Alternatives) toExtend.getAlternatives();
        } else {
            // Wrap the current alternative in an Alternatives block
            alternatives = factory.createAlternatives();
            alternatives.getElements().add(toExtend.getAlternatives());
            toExtend.setAlternatives(alternatives);
        }

        Assignment newAssignment = factory.createAssignment();
        newAssignment.setFeature(newAlternative.getName().toLowerCase());
        newAssignment.setOperator("=");


        RuleCall newCall = factory.createRuleCall();

        newCall.setRule(newAlternative);
        newAssignment.setTerminal(newCall);

        alternatives.getElements().add(newAssignment);

        toExtend.setAlternatives(alternatives);
        return toExtend;
    }


    public void redirectRuleCallsToLocalCopies(Grammar grammar, Map<AbstractRule, AbstractRule> originalToCopyMap) {
        for (AbstractRule rule : grammar.getRules()) {
            EcoreUtil2.eAllContentsAsList(rule).stream()
                    .filter(RuleCall.class::isInstance)
                    .map(RuleCall.class::cast)
                    .forEach(ruleCall -> {
                        AbstractRule calledRule = ruleCall.getRule();
                        if (originalToCopyMap.containsKey(calledRule)) {
                            // Redirect to local copy
                            ruleCall.setRule(originalToCopyMap.get(calledRule));
                        }
                    });
        }
    }


    private static String getFQName(List<String> packageNameParts, String name) {
        return getFQName(Names.constructQualifiedName(packageNameParts), name);
    }

    private static String getFQName(String packageName, String name) {
        if (packageName.isEmpty()) {
            return name;
        } else {
            return packageName + "." + name;
        }
    }

    /**
     * Caches a grammar to be loaded by {@link #loadGrammar(ASTMCQualifiedName)}.
     *
     * @param grammar the grammar to cache
     */
    private void cacheGrammar(Grammar grammar) {
        String grammarName = grammar.getName();
        loadedGrammarsCache.put(grammarName, grammar);
    }

    public Grammar compose(ASTLanguageComponent peComponent, ASTLanguageComponent reComponent, Collection<Binding> bindings) {
        ASTMCQualifiedName requiredGrammarName = reComponent.getGrammarDefinition().getMCQualifiedName();
        ASTMCQualifiedName providedGrammarName = peComponent.getGrammarDefinition().getMCQualifiedName();
        Grammar requiringGrammar = this.loadedGrammarsCache.get(requiredGrammarName.getQName());
        if (requiringGrammar == null) {
            requiringGrammar = loadGrammar(requiredGrammarName);
        }
        Grammar providingGrammar = loadGrammar(providedGrammarName);
        cacheGrammar(requiringGrammar);
        cacheGrammar(providingGrammar);

        // We assume a top down traversal of the feature tree. Since Xtext only supports single inheritance, this grammar is used as target grammar forthon.
        Grammar grammar = composeGrammars(providingGrammar, requiringGrammar, peComponent, reComponent, bindings);
        cacheGrammar(grammar);
        lastComposedGrammarName = grammar.getName();
        return grammar;
    }
}
