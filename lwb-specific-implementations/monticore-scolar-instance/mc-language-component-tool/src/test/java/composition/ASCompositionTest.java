/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package composition;

import de.monticore.generating.GeneratorSetup;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.grammar.grammar._symboltable.IGrammarGlobalScope;
import de.monticore.java.javadsl._ast.*;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.Names;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTGrammarDefinition;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentBuilder;
import languagecomponentbase.LanguageComponentBaseMill;

import languagecomponentbase._symboltable.LanguageComponentSymbol;
import org.junit.Test;
import util.Binding;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static util.Binding.BindingType.AS;

/**
 * Test for {@link MCGrammarArtifactComposerHelper}
 *
 * @author Jerome Pfeiffer
 */

public class ASCompositionTest {

    public static final Path OUTPUT_PATH = Paths.get("target", "generated-test-models");
    public static final MCPath MODEL_PATH =
            new MCPath(Collections.singletonList(
                    Paths.get("src", "test", "resources", "grammars")));

    protected static final MCPath COMPONENT_PATH = new MCPath(Paths.get("src", "test", "resources"));

/**
     * Tests composition between the two grammars Variable -> Statecharts.
     */

    @Test
    public void testSingleGrammarComposition() {

        MCGrammarArtifactComposerHelper grammarComposer = new MCGrammarArtifactComposerHelper(MODEL_PATH);

        Grammar_WithConceptsMill.reset();
        Grammar_WithConceptsMill.init();
        IGrammar_WithConceptsGlobalScope symbolTable = Grammar_WithConceptsMill.globalScope();
        symbolTable.clear();
        symbolTable.setFileExt("mc4");
        symbolTable.getSymbolPath().addEntry(Paths.get("src/test/resources/grammars"));

        final MCGrammarSymbol epGrammar = loadGrammarSymbol(symbolTable, "mc.lang.StateCharts");
        final MCGrammarSymbol ppGrammar = loadGrammarSymbol(symbolTable, "mc.lang.Variable");

        Collection<Binding> mapping = new ArrayList<>();
        mapping.add(new Binding(AS, "Var", "SCElement"));

        Map<String, String> ep2RuleSource = new HashMap<>();
        ep2RuleSource.put("X.SCElement", "mc.lang.StateCharts.SCElement");
        ep2RuleSource.put("Y.Var", "VariableDefinition");

        ASTLanguageComponent epComp = constructDummyLanguageComponentWithName("X", "mc.lang.StateCharts");
        ASTLanguageComponent ppComp = constructDummyLanguageComponentWithName("Y", "mc.lang.Variable");

        assertTrue(ppGrammar.getAstGrammar().isPresent());
        assertTrue(epGrammar.getAstGrammar().isPresent());
        grammarComposer.compose(ppGrammar.getAstGrammar().get(),
                epGrammar.getAstGrammar().get(), mapping, ep2RuleSource, ppComp, epComp);

        assertTrue(grammarComposer.getParameterHandler().isPresent());

        ASTMCGrammar result = grammarComposer.getParameterHandler().get();

        checkGrammar(result, ppGrammar.getAstGrammar().get(),
                epGrammar.getAstGrammar().get(),
                Collections.singletonList(new Binding(AS, "VariableDefinition", "SCElement")),
                false, false);

    }

/**
     * Constructs an empty Language component with the given name and grammar.
     * @param componentName Name of the component to construct
     * @param grammarName Name of the grammar to set in the component.
     * @return The constructed AST
     */

    private ASTLanguageComponent constructDummyLanguageComponentWithName(
            String componentName, String grammarName) {

        ASTMCQualifiedNameBuilder qnBuilder = MCBasicTypesMill.mCQualifiedNameBuilder();
        qnBuilder.addAllParts(Arrays.asList(grammarName.split(".")));
        ASTLanguageComponentBuilder builder =
                LanguageComponentBaseMill.languageComponentBuilder();
        builder.setName(componentName);
        List<ASTGrammarDefinition> grammars = new ArrayList<>();
        grammars.add(LanguageComponentBaseMill.grammarDefinitionBuilder().setMCQualifiedName(qnBuilder.build()).build());
        builder.setGrammarDefinitionsList(grammars);
        //builder.setGrammarDefinition(0, LanguageComponentBaseMill.grammarDefinitionBuilder().setMCQualifiedName(qnBuilder.build()).build()); // into list here
        return builder.build();
    }

/**
     * Loads the grammar symbol in the given symbolTable with the given name
     *
     * @param symbolTable The symboltable used to load the grammar
     * @param grammarName The name of the grammar to load
     * @return The loaded grammar.
     */

    private MCGrammarSymbol loadGrammarSymbol(IGrammarGlobalScope symbolTable, String grammarName){
        Optional<MCGrammarSymbol> grammarSymbolOpt = symbolTable.resolveMCGrammar(grammarName);

        assertTrue(grammarSymbolOpt.isPresent());
        assertTrue(grammarSymbolOpt.get().getAstGrammar().isPresent());

        return grammarSymbolOpt.get();
    }

/**
     * Integrates Variables -> Statecharts -> MontiArc <- OCLInvariant
     */

    @Test
    public void testMultipleGrammarsComposition() {

        MCGrammarArtifactComposerHelper grammarComposer = new MCGrammarArtifactComposerHelper(MODEL_PATH);

        Grammar_WithConceptsMill.reset();
        Grammar_WithConceptsMill.init();
        IGrammar_WithConceptsGlobalScope symbolTable = Grammar_WithConceptsMill.globalScope();
        symbolTable.clear();
        symbolTable.setFileExt("mc4");
        symbolTable.getSymbolPath().addEntry(Paths.get("src/test/resources/grammars"));

        final MCGrammarSymbol statecharts = loadGrammarSymbol(symbolTable, "mc.lang.StateCharts");
        final MCGrammarSymbol variable = loadGrammarSymbol(symbolTable, "mc.lang.Variable");
        final MCGrammarSymbol montiarc = loadGrammarSymbol(symbolTable, "mc.lang.MontiArc");
        final MCGrammarSymbol ocl = loadGrammarSymbol(symbolTable, "mc.lang.OCLInvariant");

        /////// Variable -> MontiArc
        Collection<Binding> var2SCMapping = new ArrayList<>();
        var2SCMapping.add(new Binding(AS,"Var", "SCElement"));
        Map<String, String> var2SCEP2RuleMapping = new HashMap<>();
        var2SCEP2RuleMapping.put("X.SCElement", "mc.lang.StateCharts.SCElement");
        var2SCEP2RuleMapping.put("Y.Var", "mc.lang.Variable.VariableDefinition");

        ASTLanguageComponent ppComp = constructDummyLanguageComponentWithName("Y", "mc.lang.Variable");
        ASTLanguageComponent epComp = constructDummyLanguageComponentWithName("X", "mc.lang.StateCharts");


        assertTrue(variable.getAstGrammar().isPresent());
        assertTrue(statecharts.getAstGrammar().isPresent());
        grammarComposer.compose(variable.getAstGrammar().get(),
                statecharts.getAstGrammar().get(), var2SCMapping, var2SCEP2RuleMapping, ppComp, epComp);

        assertTrue(grammarComposer.getParameterHandler().isPresent());

        ASTMCGrammar result = grammarComposer.getParameterHandler().get();

        checkGrammar(result, variable.getAstGrammar().get(),
                statecharts.getAstGrammar().get(),
                Collections.singletonList(new Binding(AS, "VariableDefinition", "SCElement")),
                false, false);

        ///// OCL -> MontiArc
        Collection<Binding> ocl2MontiArcMapping = new ArrayList<>();
        ocl2MontiArcMapping.add(new Binding(AS, "Ocl", "Invariant"));
        Map<String, String> ocl2MontiArcEP2RuleMapping = new HashMap<>();
        ocl2MontiArcEP2RuleMapping.put("Y.Ocl", "OCLInv");
        ocl2MontiArcEP2RuleMapping.put("X.Invariant", "MontiArcInvariant");

        epComp = constructDummyLanguageComponentWithName("X", "mc.lang.MontiArc");
        ppComp = constructDummyLanguageComponentWithName("Y", "mc.lang.OCLInvariant");

        assertTrue(ocl.getAstGrammar().isPresent());
        grammarComposer.compose(ocl.getAstGrammar().get(), result, ocl2MontiArcMapping,
                ocl2MontiArcEP2RuleMapping, ppComp, epComp);

        checkGrammar(
                grammarComposer.getParameterHandler().get(),
                ocl.getAstGrammar().get(),
                result,
                Collections.singletonList(
                        new Binding(AS, "OCLInv", "MontiArcInvariant")),
                false, true);
        result = grammarComposer.getParameterHandler().get();

        ////// SC -> MontiArc
        Collection<Binding> sc2MontiArcMapping = new ArrayList<>();
        sc2MontiArcMapping.add(new Binding(AS,"SC", "Behavior"));

        ppComp = constructDummyLanguageComponentWithName("X", "mc.lang.StateCharts");
        epComp = constructDummyLanguageComponentWithName("Y", "mc.lang.MontiArc");

        Map<String, String> sc2MontiArcEP2RuleMapping = new HashMap<>();
        sc2MontiArcEP2RuleMapping.put("X.SC", "SCMain");
        sc2MontiArcEP2RuleMapping.put("Y.Behavior", "BehaviorElement");

        assertTrue(montiarc.getAstGrammar().isPresent());
        grammarComposer.compose(result, montiarc.getAstGrammar().get(), sc2MontiArcMapping,
                sc2MontiArcEP2RuleMapping, ppComp, epComp);

        checkGrammar(grammarComposer.getParameterHandler().get(), result, montiarc.getAstGrammar().get(),
                Collections.singletonList(
                        new Binding(AS,"SCMain", "BehaviorElement")),
                true, false);
        result = grammarComposer.getParameterHandler().get();
    }

    private void checkGrammar(
            ASTMCGrammar grammar,
            ASTMCGrammar sourceGrammar,
            ASTMCGrammar targetGrammar,
            List<Binding> sourceProdToTargetProd,
            boolean isSourceTempResult,
            boolean isTargetTempResult) {

        System.out.println(MCBasicTypesMill.prettyPrint(grammar, true));

        String targetGrammarName = targetGrammar.getName();
        String sourceGrammarName = sourceGrammar.getName();

        for (Binding s : sourceProdToTargetProd) {

            String sourceProdName = s.getProvisionPoint();
            String targetProdName = s.getExtensionPoint();
            String composedRuleName = sourceProdName + targetProdName;

            String expectedGrammarName = targetGrammarName + "With" + sourceGrammarName;

            List<String> expectedSuperGrammars = new ArrayList<>();
            expectedSuperGrammars.add(targetGrammarName);
            expectedSuperGrammars.add(sourceGrammarName);
            List<String> expectedProds = new ArrayList<>();
            expectedProds.add(composedRuleName);
            String startProd = targetGrammar.getClassProd(0).getName();
            if (targetGrammar.isPresentStartRule()) {
                startProd = targetGrammar.getStartRule().getName();
            }

            Map<String, String> rule2Interface = new HashMap<>();
            rule2Interface.put(composedRuleName, targetProdName);
            Map<String, String> rule2SuperRule = new HashMap<>();
            rule2SuperRule.put(composedRuleName, sourceProdName);

            if (isSourceTempResult) {
                expectedSuperGrammars.remove(sourceGrammarName);
                sourceGrammar.getClassProdList().forEach(p -> expectedProds.add(p.getName()));
                sourceGrammar.getSupergrammarList().forEach(sg -> expectedSuperGrammars.add(Names.getQualifiedName(sg.getName(0), sg.getName(0))));
                        //.forEach(sg -> expectedSuperGrammars.add(Names.getQualifiedName(sg.getNameList())));
            }
            else if(isTargetTempResult) {
                expectedSuperGrammars.remove(targetGrammarName);
                targetGrammar.getClassProdList().forEach(p -> expectedProds.add(p.getName()));
                targetGrammar.getSupergrammarList()
                        .forEach(sg -> expectedSuperGrammars.add(Names.getQualifiedName(sg.getName(0), sg.getName(0))));
            }

            assertEquals(expectedGrammarName, grammar.getName());
            assertEquals(expectedSuperGrammars.size(), grammar.getSupergrammarList().size());
            assertEquals(expectedProds.size(), grammar.getClassProdList().size());
            assertEquals(startProd, grammar.getStartRule().getName());

            assertEquals(expectedGrammarName, grammar.getName());

            for (ASTClassProd prod : grammar.getClassProdList()) {
                assertTrue(expectedProds.contains(prod.getName()));
                if (rule2Interface.containsKey(prod.getName())
                        && rule2SuperRule.containsKey(prod.getName())) {
                    assertEquals(rule2Interface.get(prod.getName()),
                            (prod.getSuperInterfaceRule(0).getName()));
                    assertEquals(rule2SuperRule.get(prod.getName()), prod.getSuperRule(0).getName());
                }
            }
        }
    }

    @Test
    public void testS2TSymbolAdapterAndResolverCorrect() throws IOException {
        LanguageComponentBaseProcessor languageComponentBaseProcessor = new LanguageComponentBaseProcessor(COMPONENT_PATH);

        Optional<LanguageComponentSymbol> scComponent = languageComponentBaseProcessor.loadLanguageComponentSymbol(
                "ascomposition.CD4AAgg");
        Optional<LanguageComponentSymbol> maComponent = languageComponentBaseProcessor.loadLanguageComponentSymbol(
                "ascomposition.AutomataAgg");

        assertTrue(scComponent.isPresent());
        assertTrue(scComponent.get().isPresentAstNode());
        assertTrue(maComponent.isPresent());
        assertTrue(maComponent.get().isPresentAstNode());

        Binding binding = new Binding(AS, "CDTypeAdaption", "StateAdaption");

        MCGrammarArtifactComposerHelper helper = new MCGrammarArtifactComposerHelper(COMPONENT_PATH);

        Optional<String> symbolAdapter = helper.generateS2TSymbolAdapter("newLanguageProject",
                binding, scComponent.get().getAstNode(), maComponent.get().getAstNode(), "aut","classdiagram", OUTPUT_PATH);

        Optional<String> symbolResolver = helper.generateS2TSymbolResolver("newLanguageProject",binding,
                scComponent.get().getAstNode(),maComponent.get().getAstNode(), "aut", "classdiagramm", OUTPUT_PATH);

        assertTrue(symbolAdapter.isPresent());
        assertTrue(symbolResolver.isPresent());

        JavaDSLParser javaParser = new JavaDSLParser();
        Optional<ASTCompilationUnit> parsedAdapter = javaParser.parse_String(symbolAdapter.get());
        Optional<ASTCompilationUnit> parsedResolver = javaParser.parse_String(symbolResolver.get());

        assertTrue(parsedAdapter.isPresent());
        assertTrue(parsedResolver.isPresent());
    }
}
