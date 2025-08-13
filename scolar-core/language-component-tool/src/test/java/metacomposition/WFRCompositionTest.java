/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package metacomposition;

import de.monticore.ast.ASTNode;
import languagecomponentbase._ast.*;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import org.junit.Test;

import util.Binding;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static util.Binding.BindingType.WFR;

/**
 * Tests for the WFR composition part of the language component composition.
 *
 * @author Mutert
 * @author Pfeiffer
 *
 */
public class WFRCompositionTest extends AbstractComposerTest{


  @Test
  public void testAddedWFRSetIsPresentAfterComposition() {
    List<Binding> binding = new ArrayList<>();
    binding.add(new Binding(WFR, "TransitionsCorrect", ""));

    ASTLanguageComponentCompilationUnit languageinterface = composeLanguageComponent(
        "languagecomponents.SCCorrect",
        "languagecomponents.SC",
        binding, true);

    assertTrue(languageinterface.getLanguageComponent().getWfrSetDefinition("TransitionsCorrect").isPresent());
  }

  @Test
  public void testCorrectParameterComposition() {
    List<Binding> binding = new ArrayList<>();
    binding.add(new Binding(WFR, "TransitionsCorrect", ""));

    ASTLanguageComponentCompilationUnit languageinterface = composeLanguageComponent(
        "languagecomponents.SCCorrect",
        "languagecomponents.SC",
        binding, true);

    Set<ASTParameter> parameters = languageinterface.getLanguageComponent().getParameters();
    assertEquals(2, parameters.size());

    binding = new ArrayList<>();
    binding.add(new Binding(WFR, "BasicSCCorrectCoCos", ""));

    languageinterface = composeLanguageComponent(
        "languagecomponents.SCCorrect",
        "languagecomponents.SC",
        binding, true);

    parameters = languageinterface.getLanguageComponent().getParameters();
    assertEquals(3, parameters.size());
  }

  /**
   * Extension Point BehaviorRule is optional and feature is optional -> EP
   * Invariante is optional in composed language component.
   */
  @Test
  public void testComposeEPOptionalAndFeatureOptional() {
    List<Binding> binding = new ArrayList<>();
    binding.add(new Binding(WFR, "NewGeneralCoCos", "BasicCoCos"));
    binding.add(new Binding(WFR, "NewPortCoCo", ""));

    List<String> expectedWFRSets = new ArrayList<>();
    expectedWFRSets.add("NoInnerComponents");
    expectedWFRSets.add("BasicCocos");
    expectedWFRSets.add("NewPortCoCo");

    ASTLanguageComponentCompilationUnit languageinterface = composeLanguageComponent(
        "languagecomponents.AdditionalMontiArcCoCos",
        "languagecomponents.MontiArcCoCos",
        binding, true);

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArc");

    checkCorrectnessOfLanguageComponent(languageinterface.getLanguageComponent(), new HashMap<>(), new ArrayList<>(),
            expectedWFRSets, expectedASReferenceList, "MontiArcCoCosWithAdditionalMontiArcCoCos");
  }
  
  /**
   * Extension Point ConnectorRule is mandatory and feature is optional -> EP
   * Verhalten is optional in composed language component.
   */
  @Test
  public void testComposeEPMandatoryAndFeatureOptional() {
    List<Binding> binding = new ArrayList<>();
    binding.add(new Binding(WFR, "ConnectorCorrect", "ConnectorRule"));
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();

    List<String> expectedWFRSets = new ArrayList<>();
    expectedWFRSets.add("NoInnerComponents");
    expectedWFRSets.add("BasicCocos");

    ASTLanguageComponentCompilationUnit languageinterface = composeLanguageComponent(
        "languagecomponents.ConnectorCorrectnessCoCos",
        "languagecomponents.MontiArcCoCos",
        binding, true);

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArc");

    checkCorrectnessOfLanguageComponent(
        languageinterface.getLanguageComponent(), expectedEPs, new ArrayList<>(), expectedWFRSets,
        expectedASReferenceList, "MontiArcCoCosWithConnectorCorrectnessCoCos");
  }

  @Test
  public void testWFRBinding() {
    final String epComponentFQName =
        "general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct";
    final String ppComponentFQName = "general.montiarcexample.connectorCorrectness.ConnectorCorrectness";
    Binding binding = new Binding(
        Binding.BindingType.WFR,
        "ConnectorCorrect.ConnectorCorrect",
        "ConnectorRule");

    final LanguageComponentSymbol epComponent = loadLanguageComponentSymbol(epComponentFQName, true);
    final LanguageComponentSymbol ppComponent = loadLanguageComponentSymbol(ppComponentFQName, true);

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

    Set<String> expectedWFRSets = new HashSet<>();
    expectedWFRSets.add("BasicSCCoCos");

    String expectedComposedInterfaceName = "AutomatenArchitekturLanguageProductWithConnectorCorrectness";

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithStateCharts");

    checkCorrectnessOfLanguageComponent(
        customizedComponent.getLanguageComponent(), expectedEPs, expectedPPs, expectedWFRSets,
        expectedASReferenceList, expectedComposedInterfaceName);
  }
}
