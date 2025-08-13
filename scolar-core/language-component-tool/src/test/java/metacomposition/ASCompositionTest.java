/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package metacomposition;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import org.junit.Test;

import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTOptionality;
import util.Binding;
import util.Binding.BindingType;

import java.util.*;

/**
 * Tests composition of abstract syntax elements of language components.
 *
 * @author Jerome Pfeiffer
 */
public class ASCompositionTest extends AbstractComposerTest {
  
  /**
   * Extension Point Verhalten is mandatory and feature is mandatory as well ->
   * EP Verhalten is not available in composed language component.
   */
  @Test
  public void testComposeASEPMandatoryAndFeatureMandatory() {
    List<Binding> bindings = new ArrayList<>();
    bindings.add(new Binding(BindingType.AS, "AutKoerper", "Verhalten"));

    ASTLanguageComponentCompilationUnit composedAS = composeLanguageComponent(
        "languagecomponents.SCGrammar",
        "languagecomponents.MontiArcGrammar", bindings, true);
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Verhalten", ASTOptionality.OPTIONAL);
    expectedEPs.put("Variable", ASTOptionality.MANDATORY);
    expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);
    
    List<String> expectedPPs = Collections.singletonList("Komponente");

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithStateChart");
    
    checkCorrectnessOfLanguageComponent(
        composedAS.getLanguageComponent(), expectedEPs, expectedPPs, new HashSet<>(),
        expectedASReferenceList,"MontiArcGrammarWithSCGrammar");
  }
  
  /**
   * Extension Point Verhalten is mandatory and feature is optional -> EP
   * Verhalten is optional in composed language component.
   */
  @Test
  public void testComposeASEPMandatoryAndFeatureOptional() {
    List<Binding> bindings = new ArrayList<>();
    bindings.add(new Binding(BindingType.AS, "AutKoerper", "Verhalten"));

    ASTLanguageComponentCompilationUnit composedAS = composeLanguageComponent(
        "languagecomponents.SCGrammar",
        "languagecomponents.MontiArcGrammar",
        bindings, true);
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Variable", ASTOptionality.MANDATORY);
    expectedEPs.put("Verhalten", ASTOptionality.OPTIONAL);
    expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);
    
    List<String> expectedPPs = Collections.singletonList("Komponente");

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithStateChart");
    
    checkCorrectnessOfLanguageComponent(
        composedAS.getLanguageComponent(), expectedEPs, expectedPPs, new HashSet<>(),
         expectedASReferenceList,"MontiArcGrammarWithSCGrammar");
  }
  
  /**
   * Extension Point Invariante is optional and feature is mandatory -> EP
   * Invariante is not available in composed language component.
   */
  @Test
  public void testComposeASEPOptionalAndFeatureMandatory() {
    List<Binding> bindings = new ArrayList<>();
    bindings.add(new Binding(BindingType.AS, "OCLInv", "Invariante"));
    ASTLanguageComponentCompilationUnit composedAS = composeLanguageComponent(
        "languagecomponents.OCLInvGrammar",
        "languagecomponents.MontiArcGrammar",
        bindings, true);
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Verhalten", ASTOptionality.MANDATORY);
    expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);
    
    List<String> expectedPPs = Collections.singletonList("Komponente");

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithOCLInvariant");
    
    checkCorrectnessOfLanguageComponent(
        composedAS.getLanguageComponent(), expectedEPs, expectedPPs, new HashSet<>(),
         expectedASReferenceList,"MontiArcGrammarWithOCLInvGrammar");
  }
  
  /**
   * Extension Point Invariante is optional and feature is optional -> EP
   * Invariante is optional in composed language component.
   */
  @Test
  public void testComposeASEPOptionalAndFeatureOptional() {
    List<Binding> bindings = new ArrayList<>();
    bindings.add(new Binding(BindingType.AS, "OCLInv", "Invariante"));
    ASTLanguageComponentCompilationUnit composedComponent = composeLanguageComponent(
        "languagecomponents.OCLInvGrammar",
        "languagecomponents.MontiArcGrammar",
        bindings, true);
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Verhalten", ASTOptionality.MANDATORY);
    expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);
    
    List<String> expectedPPs = Collections.singletonList("Komponente");

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithOCLInvariant");
    
    checkCorrectnessOfLanguageComponent(
        composedComponent.getLanguageComponent(), expectedEPs, expectedPPs, new HashSet<>(),
         expectedASReferenceList,"MontiArcGrammarWithOCLInvGrammar");
  }

  @Test
  public void testASBinding() {
    final String epComponentFQName =
        "general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct";
    final String ppComponentFQName = "general.montiarcexample.invariant.OCLInvariant";
    Binding binding = new Binding(BindingType.AS, "OCLInv", "Invariante");

    final LanguageComponentSymbol epComponent = loadLanguageComponentSymbol(epComponentFQName, true);
    final LanguageComponentSymbol ppComponent = loadLanguageComponentSymbol(ppComponentFQName, true);

    ASTLanguageComponentCompilationUnitA epAST = (ASTLanguageComponentCompilationUnitA) epComponent.getEnclosingScope().getAstNode();
    ASTLanguageComponentCompilationUnitA ppAST = (ASTLanguageComponentCompilationUnitA) ppComponent.getEnclosingScope().getAstNode();


    // Compose the language components
    final ASTLanguageComponentCompilationUnit customizedComponent =
        composer.composeLanguageComponent(
                ppAST.getLanguageComponentCompilationUnit(), epAST.getLanguageComponentCompilationUnit(), Collections.singletonList(binding));

    final String printedComponent = LanguageComponentBaseMill.prettyPrint(customizedComponent, true);
    Log.debug(printedComponent, "CustomizationResolverTest#testASBinding");

    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);
    expectedEPs.put("Verhalten", ASTOptionality.OPTIONAL);
    expectedEPs.put("Verhalten2Java", ASTOptionality.OPTIONAL);
    expectedEPs.put("Invariante2Java", ASTOptionality.OPTIONAL);

    List<String> expectedPPs = new ArrayList<>();
    expectedPPs.add("Komponente");
    expectedPPs.add("Komponente2Java");

    Set<String> expectedWFRSets = new HashSet<>();
    expectedWFRSets.add("BasicSCCoCos");

    String expectedComposedInterfaceName = "AutomatenArchitekturLanguageProductWithOCLInvariant";

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithStateChartsWithOCLInvariant");

    checkCorrectnessOfLanguageComponent(
        customizedComponent.getLanguageComponent(), expectedEPs, expectedPPs, expectedWFRSets,
        expectedASReferenceList,expectedComposedInterfaceName);
  }
}
