/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package metacomposition;

import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import languagecomponentbase._ast.*;

import languagecomponentbase._symboltable.*;
import org.junit.Test;
import util.Binding;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests composition of gen.
 *
 * @author Jerome Pfeiffer
 */
public class GENCompositionTest extends AbstractComposerTest {
  
  /**
   * Extension Point PortTrafo is optional and feature is optional -> EP
   * Invariante is optional in composed component.
   */
  @Test
  public void testComposeGENEPOptionalAndFeatureOptional() {
    List<Binding> binding = new ArrayList<>();
    binding.add(new Binding(Binding.BindingType.GEN, "Port2Java", "PortTrafo"));
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Verhalten2Java", ASTOptionality.MANDATORY);
    expectedEPs.put("PortTrafo", ASTOptionality.OPTIONAL);
    expectedEPs.put("PortType2Java", ASTOptionality.MANDATORY);
    
    List<String> expectedPPs = Collections.singletonList("Komponente2Java");

    ASTLanguageComponentCompilationUnit languageinterface = composeLanguageComponent(
        "languagecomponents.PortTrafos",
        "languagecomponents.MontiArcTrafos",
        binding, false);

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArc");
    
    checkCorrectnessOfLanguageComponent(
        languageinterface.getLanguageComponent(), expectedEPs, expectedPPs, new HashSet<>(),
        expectedASReferenceList,"MontiArcTrafosWithPortTrafos");
    
    List<String> expecteddomainModels = Arrays.asList(
        "montiarc._product.Arc2JavaProduct",
        "montiarc._producer.Arc2JavaProducer",
        "statecharts.Port2JavaProducer",
        "statecharts.Port2JavaProduct");
    
    checkdomainModelImports(languageinterface, expecteddomainModels, false);

  }
  
  /**
   * Extension Point Verhalten2Java is mandatory and feature is optional -> EP
   * Verhalten is optional in composed component.
   */
  @Test
  public void testComposeGENEPMandatoryAndFeatureOptional() {
    List<Binding> binding = new ArrayList<>();
    binding.add(new Binding(Binding.BindingType.GEN, "Main2Java", "Verhalten2Java"));
    
    Map<String, ASTOptionality> expectedEPs = new HashMap<>();
    expectedEPs.put("Verhalten2Java", ASTOptionality.OPTIONAL);
    expectedEPs.put("Invariante2Java", ASTOptionality.OPTIONAL);
    expectedEPs.put("Verhalten", ASTOptionality.MANDATORY);
    expectedEPs.put("Invariante", ASTOptionality.OPTIONAL);
    expectedEPs.put("SCElementTrafo", ASTOptionality.OPTIONAL);


    List<String> expectedPPs = Arrays.asList("MACompUnit", "Komponente", "Komponente2Java");

    List<String> expectedWFRs = Arrays.asList("NoInnerComponents", "BasicCoCos");

    ASTLanguageComponentCompilationUnit languageinterface = composeLanguageComponent(
            "general.montiarcexample.statecharts.SC",
        "general.montiarcexample.montiarc.MontiArc",
        binding, false);

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithStateCharts");
    
    checkCorrectnessOfLanguageComponent(
        languageinterface.getLanguageComponent(), expectedEPs, expectedPPs, expectedWFRs,
        expectedASReferenceList,"MontiArcWithSC");
    
    List<String> expecteddomainModels = Arrays.asList(
            "general.montiarcexample.statecharts._generator._producer.SC2JavaProducer",
            "general.montiarcexample.statecharts._generator._product.SC2JavaProduct",
            "general.montiarcexample.statecharts._generator._producer.SCElement2JavaProducer",
            "general.montiarcexample.statecharts._generator._product.SCElement2JavaProduct",
            "general.montiarcexample.montiarc._generator.MA2Java",
            "general.montiarcexample.montiarc._generator.MAInv2Java"
    );
    
    checkdomainModelImports(languageinterface, expecteddomainModels, true);
  }

  private void checkdomainModelImports(
          ASTLanguageComponentCompilationUnit languageComponent,
      List<String> expecteddomainModels, boolean withSymbolResolving) {

    ILanguageComponentBaseScope scope = languageComponent.getEnclosingScope();

    List<ASTMCImportStatement> importsList = languageComponent.getMCImportStatementList();

    assertEquals(expecteddomainModels.size(), importsList.size());
    
    for (ASTMCImportStatement definition : importsList) {
      assertTrue(expecteddomainModels.contains(definition.getQName()));
    }

    Collection<ASTRequiredGenExtension> genExtensionPoints = languageComponent.getLanguageComponent().getGENExtensionPoints();
    for (ASTRequiredGenExtension genExtensionPoint : genExtensionPoints) {
      ASTProducerInterfaceRef producerInterfaceRef = genExtensionPoint.getProducerInterfaceRef(0);
      ASTProductInterfaceRef productInterfaceRef = genExtensionPoint.getProductInterfaceRef(0);
      if(withSymbolResolving) {
        Optional<DomainModelDefinitionSymbol> domainModelDefinitionSymbol = scope.resolveDomainModelDefinition(productInterfaceRef.getName());
        Optional<DomainModelDefinitionSymbol> domainModelDefinitionSymbol1 = scope.resolveDomainModelDefinition(producerInterfaceRef.getName());
        assertTrue(domainModelDefinitionSymbol.isPresent());
        assertTrue(domainModelDefinitionSymbol1.isPresent());
      }
    }


  }

  @Test
  public void testGENBinding() {

    final String epComponentFQName =
        "general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct";
    final String ppComponentFQName = "general.montiarcexample.invariant.OCLInvariant";
    Binding binding = new Binding(Binding.BindingType.GEN, "Inv.Inv2Java",
        "Invariante2Java");

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

    Set<String> expectedWFRSets = new HashSet<>();
    expectedWFRSets.add("BasicSCCoCos");

    String expectedComposedInterfaceName = "AutomatenArchitekturLanguageProductWithOCLInvariant";

    List<String> expectedASReferenceList = new ArrayList<>();
    expectedASReferenceList.add("mc.lang.MontiArcWithStateChartsWithOCLInvariant");

    checkCorrectnessOfLanguageComponent(
        customizedComponent.getLanguageComponent(), expectedEPs, expectedPPs, expectedWFRSets,
        expectedASReferenceList, expectedComposedInterfaceName);

  }
}
