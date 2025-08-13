package metacomposition;

import de.se_rwth.commons.logging.Log;
import junit.framework.TestCase;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.ASTGrammarDefinition;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._ast.ASTOptionality;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import org.junit.Test;
import util.Binding;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static util.Binding.BindingType.WFR;

public class AggregationTest extends AbstractComposerTest {

    @Test
    public void testCompositionIsEmbeddingOrAggregation() {
        List<Binding> bindings = new ArrayList<>();
        bindings.add(new Binding(Binding.BindingType.AS, "AutKoerper", "Verhalten")); // is an embedding
        bindings.add(new Binding(Binding.BindingType.AS, "AutKoerper", "Invariante")); // is an aggregation

        ASTLanguageComponentCompilationUnit composedAS = composeLanguageComponent(
                "languagecomponents.SCGrammarForAgg",
                "languagecomponents.MontiArcGrammarForAgg", bindings, true);

        assertTrue(composedAS.getLanguageComponent().hasAggregationExtensionPoints());
        assertTrue(composedAS.getLanguageComponent().hasEmbeddingExtensionPoints());

        assertTrue(composedAS.getLanguageComponent().isEmbeddingExtensionPoint("Verhalten"));
        assertTrue(composedAS.getLanguageComponent().isAggregationExtensionPoint("Invariante"));
    }

    @Test
    public void testASBindingForAggregation() {
        final String ppComponentFQName = "languagecomponents.SCGrammarForAgg";
        final String epComponentFQName = "languagecomponents.MontiArcGrammarForAgg";

        List<Binding> bindings = new ArrayList<>();
        bindings.add(new Binding(Binding.BindingType.AS, "AutKoerper", "Invariante")); // is an aggregation

        final LanguageComponentSymbol epComponent = loadLanguageComponentSymbol(epComponentFQName, true);
        final LanguageComponentSymbol ppComponent = loadLanguageComponentSymbol(ppComponentFQName, true);

        ASTLanguageComponentCompilationUnitA epAST = (ASTLanguageComponentCompilationUnitA) epComponent.getEnclosingScope().getAstNode();
        ASTLanguageComponentCompilationUnitA ppAST = (ASTLanguageComponentCompilationUnitA) ppComponent.getEnclosingScope().getAstNode();

        // Compose the language components
        final ASTLanguageComponentCompilationUnit customizedComponent =
                composer.composeLanguageComponent(
                        ppAST.getLanguageComponentCompilationUnit(), epAST.getLanguageComponentCompilationUnit(), bindings);

        final String printedComponent = LanguageComponentBaseMill.prettyPrint(customizedComponent, true);
        Log.debug(printedComponent, "CustomizationResolverTest#testASBinding");

        Map<String, ASTOptionality> expectedEPs = new HashMap<>();
        expectedEPs.put("Verhalten", ASTOptionality.MANDATORY);
        expectedEPs.put("Variable", ASTOptionality.MANDATORY);
        expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);

        List<String> expectedPPs = new ArrayList<>();
        expectedPPs.add("Komponente");
        expectedPPs.add("AutMain");
        expectedPPs.add("AutKoerper");

        Set<String> expectedWFRSets = new HashSet<>();
        expectedWFRSets.add("Test");
        expectedWFRSets.add("TransitionsCorrect");

        String expectedComposedInterfaceName = "MontiArcGrammarForAggWithSCGrammarForAgg";

        List<String> expectedASReferenceList = new ArrayList<>();
        expectedASReferenceList.add("mc.lang.MontiArc");
        expectedASReferenceList.add("mc.lang.StateChart");

        checkCorrectnessOfLanguageComponent(
                customizedComponent.getLanguageComponent(), expectedEPs, expectedPPs, expectedWFRSets,
                expectedASReferenceList,expectedComposedInterfaceName);

        System.out.println(LanguageComponentBaseMill.prettyPrint(customizedComponent, true));
    }

    @Test
    public void testGENBindingForAggregation() {
        final String epComponentFQName = "general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProductForAgg";
        final String ppComponentFQName = "general.montiarcexample.invariant.OCLInvariantForAgg";
        Binding binding = new Binding(Binding.BindingType.GEN, "Inv.Inv2Java", "Invariante2Java");

        final LanguageComponentSymbol epComponent = loadLanguageComponentSymbol(epComponentFQName, false);
        final LanguageComponentSymbol ppComponent = loadLanguageComponentSymbol(ppComponentFQName, false);

        ASTLanguageComponentCompilationUnitA epAST = (ASTLanguageComponentCompilationUnitA) epComponent.getEnclosingScope().getAstNode();
        ASTLanguageComponentCompilationUnitA ppAST = (ASTLanguageComponentCompilationUnitA) ppComponent.getEnclosingScope().getAstNode();

        ASTLanguageComponentCompilationUnit customizedComponent =
                composer.composeLanguageComponent(
                        ppAST.getLanguageComponentCompilationUnit(), epAST.getLanguageComponentCompilationUnit(), Collections.singletonList(binding));

        Map<String, ASTOptionality> expectedEPs = new HashMap<>();
        expectedEPs.put("Invariante", ASTOptionality.MANDATORY);
        expectedEPs.put("Verhalten", ASTOptionality.OPTIONAL);
        expectedEPs.put("Verhalten2Java", ASTOptionality.OPTIONAL);
        expectedEPs.put("Invariante2Java", ASTOptionality.OPTIONAL);

        List<String> expectedPPs = new ArrayList<>();
        expectedPPs.add("Komponente");
        expectedPPs.add("Komponente2Java");
        expectedPPs.add("OCLInv");
        expectedPPs.add("Inv2Java");

        Set<String> expectedWFRSets = new HashSet<>();
        expectedWFRSets.add("BasicSCCoCos");

        String expectedComposedInterfaceName = "AutomatenArchitekturLanguageProductForAggWithOCLInvariantForAgg";

        List<String> expectedASReferenceList = new ArrayList<>();
        expectedASReferenceList.add("mc.lang.MontiArcWithStateCharts");
        expectedASReferenceList.add("mc.lang.OCLInvariant");

        checkCorrectnessOfLanguageComponent(
                customizedComponent.getLanguageComponent(), expectedEPs, expectedPPs, expectedWFRSets,
                expectedASReferenceList, expectedComposedInterfaceName);

        System.out.println(LanguageComponentBaseMill.prettyPrint(customizedComponent, true));
    }

    @Test
    public void testAllGrammarsAdded() {
        final String ppComponentFQName = "languagecomponents.SCGrammarForAgg";
        final String epComponentFQName = "languagecomponents.MontiArcGrammarForAgg";

        List<Binding> bindings = new ArrayList<>();
        bindings.add(new Binding(Binding.BindingType.AS, "AutKoerper", "Invariante")); // is an aggregation

        final LanguageComponentSymbol epComponent = loadLanguageComponentSymbol(epComponentFQName, true);
        final LanguageComponentSymbol ppComponent = loadLanguageComponentSymbol(ppComponentFQName, true);

        ASTLanguageComponentCompilationUnitA epAST = (ASTLanguageComponentCompilationUnitA) epComponent.getEnclosingScope().getAstNode();
        ASTLanguageComponentCompilationUnitA ppAST = (ASTLanguageComponentCompilationUnitA) ppComponent.getEnclosingScope().getAstNode();

        // Compose the language components
        final ASTLanguageComponentCompilationUnit customizedComponent =
                composer.composeLanguageComponent(
                        ppAST.getLanguageComponentCompilationUnit(), epAST.getLanguageComponentCompilationUnit(), bindings);

        final String printedComponent = LanguageComponentBaseMill.prettyPrint(customizedComponent, true);
        Log.debug(printedComponent, "CustomizationResolverTest#testASBinding");

        List<String> expectedGrammars = new ArrayList<>();
        expectedGrammars.add("mc.lang.MontiArc");
        expectedGrammars.add("mc.lang.StateChart");

        List<String> actualGrammars = new ArrayList<>();
        for (ASTGrammarDefinition grammar : customizedComponent.getLanguageComponent().getGrammarDefinitionList()) {
            actualGrammars.add(grammar.getMCQualifiedName().toString());
        }
        //assertEquals(expectedGrammars, actualGrammars);
        assertEquals(new HashSet<>(expectedGrammars), new HashSet<>(actualGrammars));
    }
}
