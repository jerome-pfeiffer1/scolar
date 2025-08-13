/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */


package composition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTGrammarReferenceBuilder;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMCGrammarBuilder;
import de.monticore.grammar.grammar._ast.ASTRuleReferenceBuilder;
import de.monticore.grammar.grammar._ast.ASTStartRule;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.io.FileReaderWriter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import languagecomponentbase._ast.*;
import util.Binding;

/**
 * Composes two mc grammars to a new one.
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */


public class MCGrammarArtifactComposerHelper {

    private IGrammar_WithConceptsGlobalScope symboltable;

    /**
     * Stores intermediate products during grammar composition in-memory. This
     * is required for the composition of nested components (e.g., for realizing nested features
     * in a language family)
     */


    private Map<String, ASTMCGrammar> composedGrammarCache = new HashMap<>();

    private List<ParameterHandler> aggregationBindings = new ArrayList<>();

    private Optional<ASTMCGrammar> result = Optional.empty();

    private Optional<String> resultGrammarName = Optional.empty();

    /**
     * Constructor for composition.MCGrammarArtifactComposer
     *
     * @param modelPath Model path to use when resolving MontiCore grammars
     */


    public MCGrammarArtifactComposerHelper(MCPath modelPath) {
        Grammar_WithConceptsMill.reset();
        Grammar_WithConceptsMill.init();
        symboltable = Grammar_WithConceptsMill.globalScope();
        symboltable.clear();
        for (Path path : modelPath.getEntries())
            symboltable.getSymbolPath().addEntry(path);
    }

    public void outputResult(Path outputPath, String lastComposedComponentName, String lastGrammarPackageName) throws IOException {
        if (result.isPresent()) {
          final ASTMCGrammar grammarToPrint = result.get().deepClone();
          grammarToPrint.setName(resultGrammarName.get());
          Path output = Paths.get(
                  outputPath.toString(),
                  lastComposedComponentName + "/src/main/grammars/",
                  lastGrammarPackageName,
                  resultGrammarName.get() + ".mc4");
          FileReaderWriter.storeInFile(output, MCBasicTypesMill.prettyPrint(grammarToPrint, true));
        } else {
            for(ParameterHandler parameterHandler : aggregationBindings) {
                generateS2TSymbolAdapter(parameterHandler,lastComposedComponentName,outputPath);
                generateS2TSymbolResolver(parameterHandler, lastComposedComponentName, outputPath);
            }
        }
    }

    public void compose(
            ASTLanguageComponent ppComponent,
            ASTLanguageComponent epComponent,
            Collection<Binding> bindings,
            String composedComponentName,
            String composedGrammarName) {

        Optional<ASTMCGrammar> sourceGrammar = loadGrammar(ppComponent.getASReference());
        Optional<ASTMCGrammar> targetGrammar = loadGrammar(epComponent.getASReference());

        Map<String, String> ep2RuleSource = resolveRulesFromBinding(ppComponent, epComponent, bindings);

        if (sourceGrammar.isPresent() && targetGrammar.isPresent()) {
            compose(
                    sourceGrammar.get(),
                    targetGrammar.get(),
                    bindings,
                    ep2RuleSource,
                    ppComponent,
                    epComponent);
        } else if (sourceGrammar.isPresent()) {
            Log.error("MC-GrammarComposer: target grammar for source grammar " + sourceGrammar.get().getName() + " not found!");
        } else if (targetGrammar.isPresent()) {
            Log.error("MC-GrammarComposer: source grammar for target grammar " + targetGrammar.get().getName() + " not found!");
        }

        resultGrammarName = Optional.of(Names.getSimpleName(composedGrammarName));
    }

    /**
     * Integrates all productions contained in bindings into the target grammar and
     * returns it,
     *
     * @param source,       the grammar the production to integrate comes from
     * @param target,       the grammar where the source production is to be integrated
     *                      in
     * @param bindings      of simple name of source production -> target interface
     * @param ep2RuleSource
     */


    protected void compose(
            ASTMCGrammar source,
            ASTMCGrammar target,
            Collection<Binding> bindings,
            Map<String, String> ep2RuleSource,
            ASTLanguageComponent ppComponent,
            ASTLanguageComponent epComponent) {

        List<ASTClassProd> newRules =
                getComposedClassProds(source, bindings, ep2RuleSource, ppComponent, epComponent);

        ASTMCGrammar composedGrammar = buildComposedGrammar(source, target, newRules);

        result = Optional.of(composedGrammar);

        // Cache composed grammar
        cacheGrammar(composedGrammar);
    }

    /**
     * Returns the composed productions that implement the target interface and
     * extend the source grammars rule.
     *
     * @param source        the grammar the rule of the provision point comes from
     * @param mapping       of simple name of source production -> target interface
     * @param ep2RuleSource
     * @return
     */


    private List<ASTClassProd> getComposedClassProds(
            ASTMCGrammar source,
            Collection<Binding> mapping,
            Map<String, String> ep2RuleSource,
            ASTLanguageComponent ppComponent,
            ASTLanguageComponent epComponent) {

        List<ASTClassProd> newRules = new ArrayList<>();

        final List<Binding> asBindings = mapping.stream()
                .filter(b -> b.getBindingType().equals(Binding.BindingType.AS))
                .collect(Collectors.toList());

        for (Binding binding : asBindings) {

            String ppRuleName = Names.getSimpleName(
                    ep2RuleSource.get(ppComponent.getName() + "." + binding.getProvisionPoint()));
            String epRuleName = Names.getSimpleName(
                    ep2RuleSource.get(epComponent.getName() + "." + binding.getExtensionPoint()));

            Optional<ASTClassProd> ppRule = findRuleWithNameInGrammar(source, ppRuleName);

            // go through source productions to copy the right-hand side of the
            // provided production if found
            if (ppRule.isPresent()) {
                // copy rule
                ASTClassProd newRule = ppRule.get().deepClone();
                newRule.setName(ppRuleName + epRuleName);
                ASTRuleReferenceBuilder ruleReferenceBuilder = Grammar_WithConceptsMill.ruleReferenceBuilder();
                // let the new rule extend the pp rule
                newRule.addSuperRule(ruleReferenceBuilder.setName(ppRuleName).build());

                // and implement the ep interface rule
                newRule.addSuperInterfaceRule(ruleReferenceBuilder.setName(epRuleName).build());

                // add to rule set of composed grammar
                newRules.add(newRule);
            }

            if (result.isPresent()) {
                newRules.addAll(result.get().getClassProdList());
            }
        }
        return newRules;

    }

    /**
     * Determine the rule with the given name in the given grammar
     *
     * @param grammar The grammar to check
     * @param name    The name of the rule to search for
     * @return The found rule, if present
     */


    private Optional<ASTClassProd> findRuleWithNameInGrammar(
            ASTMCGrammar grammar, String name) {

        for (ASTClassProd classProd : grammar.getClassProdList()) {
            if (classProd.getName().equals(name)) {
                return Optional.of(classProd);
            }
        }

        for (ASTGrammarReference grammarReference : grammar.getSupergrammarList()) {
            Optional<ASTMCGrammar> superGrammar =
                    loadGrammar(Names.constructQualifiedName(grammarReference.getNameList()));
            if (superGrammar.isPresent()) {
                Optional<ASTClassProd> foundRule = findRuleWithNameInGrammar(
                        superGrammar.get(), name
                );
                if (foundRule.isPresent()) {
                    return foundRule;
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Creates new grammar that extends source and target grammars and contains
     * the new rules. If a result is present already, we add its supergrammars +
     * rules as well.
     *
     * @param source   grammar of the provision point
     * @param target   grammar of the extension point
     * @param newRules the set of composed rules that are added to the new
     *                 synthesized grammar
     * @return
     */

    private ASTMCGrammar buildComposedGrammar(
            ASTMCGrammar source,
            ASTMCGrammar target,
            List<ASTClassProd> newRules) {

        ASTMCGrammarBuilder mCGrammarBuilder = Grammar_WithConceptsMill.mCGrammarBuilder();

        if (result.isPresent()) {
            mCGrammarBuilder.addAllSupergrammar(result.get().getSupergrammarList());
        }

        ASTGrammarReference referenceToSourceGrammar = buildGrammarReference(source);
        ASTGrammarReference referenceToTargetGrammar = buildGrammarReference(target);

        if (!isGrammarAlreadyExtended(referenceToSourceGrammar,
                mCGrammarBuilder.getSupergrammarList())
                && !isResult(referenceToSourceGrammar)) {
            // extend the pp's grammar
            mCGrammarBuilder.addSupergrammar(referenceToSourceGrammar);
        }
        // set package to the ep's grammar one
        mCGrammarBuilder.setPackageList(new ArrayList<>(target.getPackageList()));

        // Problem with changing the package could be solved here somewhere

        // add all rules of the new rule set
        mCGrammarBuilder.addAllClassProds(newRules);

        final String targetGrammarFQName = getFQName(target.getPackageList(), target.getName());

        final boolean targetAlreadyPresent =
                mCGrammarBuilder.getSupergrammarList()
                        .stream()
                        .map(g -> Names.constructQualifiedName(g.getNameList()))
                        .distinct()
                        .anyMatch(g -> g.equals(targetGrammarFQName));

        if (!isResult(referenceToTargetGrammar) && !targetAlreadyPresent) {
            mCGrammarBuilder.addSupergrammar(referenceToTargetGrammar);
        }
        if (target.getName().equals(source.getName())) {
            mCGrammarBuilder.setName(target.getName());
        } else {
            mCGrammarBuilder.setName(target.getName() + "With" + source.getName());
        }

        mCGrammarBuilder.setStartRule(getStartRule(target));

        return mCGrammarBuilder.build();
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
     * TODO: Write me!
     *
     * @param referenceToSourceGrammar
     * @return
     */
    private boolean isResult(ASTGrammarReference referenceToSourceGrammar) {

        if (result.isPresent()) {
            String resultName = getFQName(result.get().getPackageList(), result.get().getName());
            String referenceName = Names.constructQualifiedName(referenceToSourceGrammar.getNameList());
            return resultName.equals(referenceName);
        }
        return false;
    }

    /**
     * Checks whether grammar toCheck is contained in list superGrammarList
     *
     * @param toCheck
     * @param superGrammarList
     * @return
     */
    private boolean isGrammarAlreadyExtended(
            ASTGrammarReference toCheck,
            List<ASTGrammarReference> superGrammarList) {

        for (ASTGrammarReference reference : superGrammarList) {
            if (Names.constructQualifiedName(reference.getNameList())
                    .equals(Names.constructQualifiedName(toCheck.getNameList()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the start rule of the passed grammar if present. Otherwise the
     * first rule is returned.
     *
     * @param grammar The grammar to determine the start rules of
     * @return The determined start rules
     */
    private ASTStartRule getStartRule(ASTMCGrammar grammar) {
        if (!grammar.isPresentStartRule()) {
            String name = grammar.getClassProdList().get(0).getName();
            final ASTStartRule startRule = Grammar_WithConceptsMill.startRuleBuilder().setName(name).build();
            return startRule;
        } else {
            return grammar.getStartRule();
        }
    }

    /**
     * Creates a grammar reference.
     *
     * @param grammar The grammar to create a reference for
     * @return The reference to the given grammar
     */
    private ASTGrammarReference buildGrammarReference(ASTMCGrammar grammar) {
        ASTGrammarReferenceBuilder grammarReferenceBuilder =
                Grammar_WithConceptsMill.grammarReferenceBuilder();
        List<String> fqn = new ArrayList<>(grammar.getPackageList());
        fqn.add(grammar.getName());
        return grammarReferenceBuilder.setNamesList(fqn).build();
    }

    /**
     * Returns a Map from ep/pp name -> full qualified rule name
     *
     * @param source
     * @param target
     * @param bindings
     * @return
     */
    protected Map<String, String> resolveRulesFromBinding(
            ASTLanguageComponent source,
            ASTLanguageComponent target,
            Collection<Binding> bindings) {

        Map<String, String> result = new HashMap<>();

        final List<Binding> asBindings = bindings.stream()
                .filter(b -> b.getBindingType().equals(Binding.BindingType.AS))
                .collect(Collectors.toList());

        for (Binding b : asBindings) {

            String epName = b.getExtensionPoint();
            String ppName = b.getProvisionPoint();

            Optional<ASTRequiredGrammarExtension> require =
                    target.getLanguageComponentElementList()
                            .stream()
                            .filter(li -> li instanceof ASTRequiredGrammarExtension)
                            .map(li -> (ASTRequiredGrammarExtension) li)
                            .filter(li -> li.getName().equals(epName))
                            .findFirst();

            if (!require.isPresent()) {
                Log.error(String.format("No grammar extension point '%s' in component '%s'. Required by binding '%s'", epName, target.getName(), b.toString()));
                continue;
            }
            String epAstReference = require.get().getReferencedRule().toString();

            Optional<ASTProvidedGrammarExtension> provide =
                    source.getLanguageComponentElementList()
                            .stream()
                            .filter(li -> li instanceof ASTProvidedGrammarExtension)
                            .map(li -> (ASTProvidedGrammarExtension) li)
                            .filter(li -> li.getName().equals(ppName))
                            .findFirst();

            if (!provide.isPresent()) {
                Log.error(String.format("No grammar provision point '%s' in component '%s'. Required by binding '%s'", ppName, source.getName(), b.toString()));
                continue;
            }
            String ppAstReference = provide.get().getReferencedRule().toString();

            result.put(target.getName() + "." + epName, epAstReference);
            result.put(source.getName() + "." + ppName, ppAstReference);

        }
        return result;
    }

    /**
     * @return result
     */
    public Optional<ASTMCGrammar> getParameterHandler() {
        return this.result;
    }

    /**
     * Loads the grammar with the given name.<br>
     * Cached grammars have precedence over grammars on the
     * model path.
     *
     * @param name Qualified name of the grammar to load
     * @return The loaded grammar, if found.
     */
    private Optional<ASTMCGrammar> loadGrammar(String name) {
        /*
         *  When a feature diagram has nested features grammar artifacts are not output
         *  after each composition step. But in a next composition step the algorithm tries to
         *  load a composed grammar using this method. Therefore, we cache composed grammars
         *  in-memory (e.g., FooWithBar.mc4)
         */

        // First, check if cached grammar exists
        if (this.composedGrammarCache.containsKey(name)) {
            return Optional.of(composedGrammarCache.get(name));
        }
        // Second, resolve grammar from model path
        Optional<MCGrammarSymbol> symbol = symboltable.resolveMCGrammar(name);

        if (symbol.isPresent()) {
            return symbol.get().getAstGrammar();
        } else {
            return Optional.empty();
        }
    }

    /**
     * Caches a grammar to be loaded by {@link #loadGrammar(String)}.
     *
     * @param grammar the grammar to cache
     */
    private void cacheGrammar(ASTMCGrammar grammar) {
        String qualifiedGrammarName = getFQName(grammar.getPackageList(), grammar.getName());
        composedGrammarCache.put(qualifiedGrammarName, grammar);
    }

    /**
     * Method for generating the Symbol Resolver class from the Freemarker template S2TSymbolResolver with filled in placeholder.
     *
     * @param grammarPackage
     * @param binding
     * @param ppComponent
     * @param epComponent
     * @param outputPath
     * @return
     * @throws IOException
     */
    public Optional<String> generateS2TSymbolResolver(String grammarPackage, Binding binding,
                                                      ASTLanguageComponent ppComponent,
                                                      ASTLanguageComponent epComponent,
                                                      String ppPackageName,
                                                      String epPackageName,
                                                      Path outputPath) throws IOException {
        String epName = binding.getExtensionPoint();
        String ppName = binding.getProvisionPoint();

        Optional<ASTProvidedGrammarExtension> provide = ppComponent.getGrammarProvisionPoint(ppName);
        Optional<ASTRequiredGrammarExtension> require = epComponent.getGrammarExtensionPoint(epName);

        if (provide.isPresent() && require.isPresent()) {

            ParameterHandler parameterHandler =
                    getParameterHandler(ppComponent, epComponent, ppPackageName, epPackageName, provide, require);

            String targetKindInterface = epPackageName + "." +  parameterHandler.getEpGrammar().toLowerCase()
                    + "._symboltable." + "I" + Names.getSimpleName(parameterHandler.getEpProduct()) + "SymbolResolver";
            String pathSourceMill = ppPackageName + "." + parameterHandler.getPpGrammar().toLowerCase()
                    + "." + parameterHandler.getPpGrammar() + "Mill";
            String sourceMill = parameterHandler.getPpGrammar() + "Mill";

            Path productPath = Paths.get(outputPath.toString(),
                    Names.getPathFromPackage(grammarPackage + "/src/main/java/_symboltable/"),
                    Names.getSimpleName(parameterHandler.getPpProduct()) + "2" +
                            Names.getSimpleName(parameterHandler.getEpProduct()) + "Resolver.java");

            String symbolResolver = generateSymbolResolverCode(
                    Names.getSimpleName(parameterHandler.getPpProduct()),
                    Names.getSimpleName(parameterHandler.getEpProduct()),
                    parameterHandler.getGrammarPackagePath(),
                    parameterHandler.getSourceKindSymbolClass(),
                    parameterHandler.getTargetKindSymbolClass(),
                    targetKindInterface,
                    pathSourceMill,
                    sourceMill,
                    productPath);

            return Optional.of(symbolResolver);
        }
        return Optional.empty();
    }

    public void generateS2TSymbolResolver(ParameterHandler parameterHandler, String projectName,
                                          Path outputPath) {


            String targetKindInterface = parameterHandler.getEpProduct() + "." +  parameterHandler.getEpGrammar().toLowerCase()
                    + "._symboltable." + "I" + Names.getSimpleName(parameterHandler.getEpProduct()) + "SymbolResolver";
            String pathSourceMill = parameterHandler.getPpProduct() + "." + parameterHandler.getPpGrammar().toLowerCase()
                    + "." + parameterHandler.getPpGrammar() + "Mill";
            String sourceMill = parameterHandler.getPpGrammar() + "Mill";

            Path productPath = Paths.get(outputPath.toString(),
                    Names.getPathFromPackage(projectName + "/src/main/java/_symboltable/"),
                    Names.getSimpleName(parameterHandler.getPpProduct()) + "2" +
                            Names.getSimpleName(parameterHandler.getEpProduct()) + "Resolver.java");

            generateSymbolResolverCode(
                    Names.getSimpleName(parameterHandler.getPpProduct()),
                    Names.getSimpleName(parameterHandler.getEpProduct()),
                    parameterHandler.getGrammarPackagePath(),
                    parameterHandler.getSourceKindSymbolClass(),
                    parameterHandler.getTargetKindSymbolClass(),
                    targetKindInterface,
                    pathSourceMill,
                    sourceMill,
                    productPath);
    }

    /**
     * Method for generating the Symbol Adapter class from the Freemarker template S2TSymbolAdapter with filled in placeholder.
     *
     * @param grammarPackage
     * @param binding
     * @param ppComponent
     * @param epComponent
     * @param outputPath
     * @return
     */
    public Optional<String> generateS2TSymbolAdapter(String grammarPackage, Binding binding,
                                                     ASTLanguageComponent ppComponent,
                                                     ASTLanguageComponent epComponent,
                                                     String ppPackageName,
                                                     String epPackageName,
                                                     Path outputPath) {

        String epName = binding.getExtensionPoint();
        String ppName = binding.getProvisionPoint();

        Optional<ASTProvidedGrammarExtension> provide = ppComponent.getGrammarProvisionPoint(ppName);
        Optional<ASTRequiredGrammarExtension> require = epComponent.getGrammarExtensionPoint(epName);

        if (provide.isPresent() && require.isPresent()) {
            ParameterHandler parameterHandler =
                    getParameterHandler(ppComponent, epComponent, ppPackageName, epPackageName, provide, require);

            Path productPath = Paths.get(outputPath.toString(),
                    Names.getPathFromPackage(grammarPackage + "/src/main/java/_symboltable/"),
                    Names.getSimpleName(parameterHandler.getPpProduct()) + "2" +
                            Names.getSimpleName(parameterHandler.getEpProduct()) + "Adapter.java");

            String symbolAdapter = generateSymbolAdapterCode(
                    Names.getSimpleName(parameterHandler.getPpProduct()),
                    Names.getSimpleName(parameterHandler.getEpProduct()),
                    parameterHandler.getGrammarPackagePath(),
                    parameterHandler.getTargetKindSymbolClass(),
                    parameterHandler.getSourceKindSymbolClass(),
                    productPath);

            return Optional.of(symbolAdapter);
        }
        return Optional.empty();
    }

    public void generateS2TSymbolAdapter(ParameterHandler parameterHandler, String projectName,
                                         Path outputPath) {

            Path productPath = Paths.get(outputPath.toString(),
                    Names.getPathFromPackage(projectName + "/src/main/java/_symboltable/"),
                    Names.getSimpleName(parameterHandler.getPpProduct()) + "2" +
                            Names.getSimpleName(parameterHandler.getEpProduct()) + "Adapter.java");

            generateSymbolAdapterCode(
                    Names.getSimpleName(parameterHandler.getPpProduct()),
                    Names.getSimpleName(parameterHandler.getEpProduct()),
                    parameterHandler.getGrammarPackagePath(),
                    parameterHandler.getTargetKindSymbolClass(),
                    parameterHandler.getSourceKindSymbolClass(),
                    productPath);
    }

    private String generateSymbolResolverCode(String source,
                                              String target,
                                              String grammarPackagePath,
                                              String sourceKindSymbolClass,
                                              String targetKindSymbolClass,
                                              String targetKindResolverInterface,
                                              String pathSourceMill,
                                              String sourceMill,
                                              Path productPath) {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(getClass(), "/freemarker");
        cfg.setDefaultEncoding("UTF-8");

        Map<String, Object> input = new HashMap<>();
        input.put("Source", source);
        input.put("Target", target);
        input.put("package", grammarPackagePath);
        input.put("SourceKindSymbolClass", sourceKindSymbolClass);
        input.put("TargetKindSymbolClass", targetKindSymbolClass);
        input.put("TargetKindResolverInterface",targetKindResolverInterface);
        input.put("PathSourceMill", pathSourceMill);
        input.put("SourceMill", sourceMill);

        try {
            Template template = cfg.getTemplate("S2TSymbolResolver.ftl");
            Writer fileWriter = new FileWriter(productPath.toString());

            //template.process(input, new OutputStreamWriter(System.out));
            template.process(input, fileWriter);
            fileWriter.close();
            String result = new String(Files.readAllBytes(productPath), StandardCharsets.UTF_8);
            return result;
        } catch (IOException | TemplateException e) {
            Log.error("Error during generation of Symbol Resolver", e);
        }
        return "";
    }

    private String generateSymbolAdapterCode(String source,
                                             String target,
                                             String grammarPackagePath,
                                             String sourceKindSymbolClass,
                                             String targetKindSymbolClass,
                                             Path productPath) {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(getClass(), "/freemarker");
        cfg.setDefaultEncoding("UTF-8");

        Map<String, Object> input = new HashMap<>();
        input.put("Source", source);
        input.put("Target", target);
        input.put("package", grammarPackagePath);
        input.put("SourceKindSymbolClass", sourceKindSymbolClass);
        input.put("TargetKindSymbolClass", targetKindSymbolClass);

        try {
            Template template = cfg.getTemplate("S2TSymbolAdapter.ftl");
            Writer fileWriter = new FileWriter(productPath.toString());

            //template.process(input, new OutputStreamWriter(System.out));
            template.process(input, fileWriter);
            fileWriter.close();
            return new String(Files.readAllBytes(productPath), StandardCharsets.UTF_8);
        } catch (IOException | TemplateException e) {
            Log.error("Error during generation of Symbol Adapter", e);
        }
        return "";
    }

    public void aggregate(Binding binding, ASTLanguageComponent ppComponent,
                          ASTLanguageComponent epComponent, String ppPackageName, String epPackageName, Path outputPath) {

        String epName = binding.getExtensionPoint();
        String ppName = binding.getProvisionPoint();

        Optional<ASTProvidedGrammarExtension> provide = ppComponent.getGrammarProvisionPoint(ppName);
        Optional<ASTRequiredGrammarExtension> require = epComponent.getGrammarExtensionPoint(epName);

        if (provide.isPresent() && require.isPresent()) {
            ParameterHandler parameterHandler =
                    getParameterHandler(ppComponent, epComponent, ppPackageName, epPackageName, provide, require);
            aggregationBindings.add(parameterHandler);
        }
    }

    private static ParameterHandler getParameterHandler(ASTLanguageComponent ppComponent,
                                                        ASTLanguageComponent epComponent,
                                                        String ppPackageName,
                                                        String epPackageName,
                                                        Optional<ASTProvidedGrammarExtension> provide,
                                                        Optional<ASTRequiredGrammarExtension> require) {
        String grammarPackagePath = "_symboltable";

        String ppProduct = provide.get().getReferencedRule();
        String epProduct = require.get().getReferencedRule();

        String ppGrammar = ppComponent.getReferencedGrammarName();
        String epGrammar = epComponent.getReferencedGrammarName();

        String sourceKindSymbolClass = ppPackageName + "." + ppGrammar.toLowerCase() + "._symboltable." + Names.getSimpleName(ppProduct) + "Symbol";
        String targetKindSymbolClass = epPackageName + "." + epGrammar.toLowerCase() + "._symboltable." + Names.getSimpleName(epProduct) + "Symbol";
        return new ParameterHandler(grammarPackagePath, ppProduct, epProduct, ppGrammar, epGrammar, sourceKindSymbolClass, targetKindSymbolClass);
    }

    public static class ParameterHandler {

        public final String grammarPackagePath;
        public final String ppProduct;
        public final String epProduct;
        public final String ppGrammar;
        public final String epGrammar;
        public final String sourceKindSymbolClass;
        public final String targetKindSymbolClass;

        private ParameterHandler(String grammarPackagePath, String ppProduct, String epProduct,
                                 String ppGrammar, String epGrammar, String sourceKindSymbolClass,
                                 String targetKindSymbolClass) {

            this.grammarPackagePath = grammarPackagePath;
            this.ppProduct = ppProduct;
            this.epProduct = epProduct;
            this.ppGrammar = ppGrammar;
            this.epGrammar = epGrammar;
            this.sourceKindSymbolClass = sourceKindSymbolClass;
            this.targetKindSymbolClass = targetKindSymbolClass;
        }

        public String getGrammarPackagePath() {
            return grammarPackagePath;
        }

        public String getPpProduct() {
            return ppProduct;
        }

        public String getEpProduct() {
            return epProduct;
        }

        public String getPpGrammar() {
            return ppGrammar;
        }

        public String getEpGrammar() {
            return epGrammar;
        }

        public String getSourceKindSymbolClass() {
            return sourceKindSymbolClass;
        }

        public String getTargetKindSymbolClass() {
            return targetKindSymbolClass;
        }
    }
}
