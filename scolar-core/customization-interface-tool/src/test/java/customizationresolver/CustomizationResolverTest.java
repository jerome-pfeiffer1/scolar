/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationresolver;

import composition.AbstractArtifactComposer;
import customizationconfiguration.CustomizationConfigurationMill;
import customizationconfiguration.CustomizationConfigurationProcessor;
import customizationconfiguration._ast.ASTCCompilationUnitA;
import customizationconfiguration._symboltable.CustomizationConfigurationGlobalScope;
import customizationconfiguration._symboltable.CustomizationConfigurationSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import junit.framework.TestCase;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.*;
import languagecomponentbase._parser.LanguageComponentBaseParser;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import metacomposition.BaseLanguageComponentComposer;
import org.junit.Before;
import org.junit.Test;
import util.Binding;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertFalse;

/**
 * Tests the Customization Resolver
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public class CustomizationResolverTest {


  public static final Path OUTPUT_PATH = Paths.get("target/generated-test-models");

  private CustomizationConfigurationGlobalScope symboltable;

  private CustomizationResolver resolver;
  private LanguageComponentBaseProcessor processor;
  private BaseLanguageComponentComposer composer;

  public static final MCPath MODEL_PATH = new MCPath(
          Paths.get("src/test/resources/"),
          Paths.get("src/test/resources/", "sourcemodels"),
          Paths.get("src/test/resources/", "targetmodels")
  );
  private CustomizationConfigurationProcessor ccProcessor;

  @Before
  public void setUp() {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
    CustomizationConfigurationMill.reset();
    CustomizationConfigurationMill.init();


    symboltable = new CustomizationConfigurationGlobalScope(MODEL_PATH, ".*");
    
    processor = new LanguageComponentBaseProcessor(symboltable);
    composer = new BaseLanguageComponentComposer(
        new TestArtifactComposer(null, null),
        processor);
    
    resolver = new CustomizationResolver(
        composer, MODEL_PATH, OUTPUT_PATH);
    ccProcessor = new CustomizationConfigurationProcessor(MODEL_PATH);
  }
  
  @Test
  public void testCustomizationResolving() {

//    Optional<LanguageComponentSymbol> languageComponentSymbol =
//        processor.loadLanguageComponentSymbolWithoutCoCos(
//            "general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct");

    Optional<LanguageComponentSymbol> languageComponentSymbol = symboltable.resolveLanguageComponent("general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct");
    assertTrue(languageComponentSymbol.isPresent());
    assertTrue(languageComponentSymbol.get().getEnclosingScope().isPresentAstNode());

    final String ccName = "general.montiarcexample.customizationconfiguration.MAA";

    Optional<ASTLanguageComponentCompilationUnit> customizedLanguageComponent =
        resolver.resolveCustomization(ccName);
    assertTrue(customizedLanguageComponent.isPresent());

    String prettyPrintPP = LanguageComponentBaseMill.prettyPrint(customizedLanguageComponent.get(), true);
    System.out.println(prettyPrintPP);

    LanguageComponentBaseParser languageComponentParser = new LanguageComponentBaseParser();
    try {
      final Optional<ASTLanguageComponentCompilationUnitA> astLanguageComponentCompilationUnit =
          languageComponentParser.parse_String(prettyPrintPP);
      assertTrue(astLanguageComponentCompilationUnit.isPresent());
      assertFalse(languageComponentParser.hasErrors());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }

    final ASTLanguageComponent languageComponent = customizedLanguageComponent.get().getLanguageComponent();
    final String expectedComponentName =
        "AutomatenArchitekturLanguageProduct" +
            "WithOCLInvariant" + "Customized"; // TODO Check if resulting name is actually wanted
    assertEquals(expectedComponentName, languageComponent.getName());
  }

  /**
   * Load the language component with the given name. Asserts the component is present.
   * @param qualifiedName Name of component to laod
   * @return The loaded component.
   */
  private ASTLanguageComponent loadLanguageComponent(String qualifiedName) {

    Optional<LanguageComponentSymbol> epComponentSymbol =
        composer
            .getLanguageComponentProcessor()
            .loadLanguageComponentSymbol(qualifiedName);
    assertTrue("Language component " + qualifiedName + " could not be loaded.",
        epComponentSymbol.isPresent());
    assertTrue(epComponentSymbol.get().isPresentAstNode());
    return epComponentSymbol.get().getAstNode();
  }

  
  @Test
  public void testParameterAssignment() {
    final String epComponentFQName =
        "general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct";
    final String customizationConfigName =
        "general.montiarcexample.customizationconfiguration.MAA";

    final ASTLanguageComponent languageComponent = loadLanguageComponent(epComponentFQName);

    Optional<CustomizationConfigurationSymbol> cc =
        ccProcessor.loadCCSymbol(customizationConfigName);
    assertTrue(cc.isPresent());
    assertTrue(cc.get().isPresentAstNode());

    final ASTLanguageComponent customizedComponent = resolver.handleParameterAssignment(
        languageComponent,
        cc.get().getAstNode());


    System.out.println(LanguageComponentBaseMill.prettyPrint(customizedComponent, true));
    
    assertTrue(customizedComponent.getParameters().isEmpty());
  }

  /**
   * Checks whether the given language component fits the expected characteristics.
   *
   * @param languageComponent Language component to check
   * @param expectedEPs EPs that are expected to be present in the language component
   * @param expectedPPs PPs that are expected to be present in the language component
   * @param expectedWFRSets
   * @param expectedASReference First abstract syntax reference that is expected to be present
   * @param expectedComposedComponentName Expected name of the language component
   */
  protected void checkCorrectnessOfLanguageComponent(
      ASTLanguageComponent languageComponent,
      Map<String, ASTOptionality> expectedEPs,
      Collection<String> expectedPPs,
      Collection<String> expectedWFRSets,
      String expectedASReference,
      String expectedComposedComponentName) {

    assertEquals(expectedComposedComponentName, languageComponent.getName());
    assertEquals(
        expectedASReference,
        languageComponent.getGrammarDefinition().getMCQualifiedName().toString());


    // ===== Extension Points =====
    Collection<ASTRequiredExtension> extensionPoints =
        languageComponent.getExtensionPoints();
    assertEquals(expectedEPs.keySet().size(), extensionPoints.size());


    final Set<String> existingEPNames =
        extensionPoints.stream()
            .map(ASTRequiredExtension::getName)
            .collect(Collectors.toSet());
    for (ASTRequiredExtension extensionPoint : extensionPoints) {
      TestCase.assertTrue(expectedEPs.containsKey(extensionPoint.getName()));
      ASTOptionality expectedOptionality = expectedEPs.get(extensionPoint.getName());
      assertEquals(
          "Expected that EP:" + extensionPoint.getName() + " is " + expectedOptionality
              + " but was " + extensionPoint.getOptionality(),
          expectedOptionality, extensionPoint.getOptionality());
    }

    // ===== Provision Points =====
    Collection<ASTProvidedExtension> providePoints =
        languageComponent.getProvisionPoints();
    assertEquals(expectedPPs.size(), providePoints.size());

    final Set<String> existingPPNames =
        providePoints.stream()
            .map(ASTProvidedExtension::getName)
            .collect(Collectors.toSet());

    // Check all expected present
    for (ASTProvidedExtension provisionPoint : providePoints) {
      assertTrue(expectedPPs.contains(provisionPoint.getName()));
    }
    // Check no more than expected present
    for (final String existingPPName : existingPPNames) {
      assertTrue(expectedPPs.contains(existingPPName));
    }

    // ===== WFR Sets ======
    final Collection<ASTWfrSetDefinition> existingWFRSets =
        languageComponent.getWfrSetDefinitions();
    assertEquals(expectedWFRSets.size(), existingWFRSets.size());
    final Set<String> existingWFRSetNames =
        existingWFRSets.stream()
            .map(ASTWfrSetDefinition::getName)
            .collect(Collectors.toSet());

    // Check all expected present
    for (final String expectedWFRSet : expectedWFRSets) {
      assertTrue(
          String.format("The expected wfr set with the name %s " +
              "was not found in the model.", expectedWFRSet),
          existingWFRSetNames.contains(expectedWFRSet));
    }
    // Check no more than expected present
    for (final String existingWFRSetName : existingWFRSetNames) {
      assertTrue(expectedWFRSets.contains(existingWFRSetName));
    }
  }

  @Test
  /**
   * Test that there is no composition in case the referenced language component
   * in the customization configuration does not match the given langugage component's name.
   */
  public void testCustomizeWrongComponent() {
    Optional<LanguageComponentSymbol> componentSymbol =
        symboltable.resolveLanguageComponent(
            "general.montiarcexample.AutomatenArchitekturLanguageProduct");
    assertTrue(componentSymbol.isPresent());
    assertTrue(componentSymbol.get().getEnclosingScope().isPresentAstNode());

    Optional<CustomizationConfigurationSymbol> cc =
        ccProcessor.loadCCSymbol("general.montiarcexample.customizationconfiguration.MAA");
    assertTrue(cc.isPresent());
    assertTrue(cc.get().isPresentAstNode());

    final ASTCCompilationUnitA astcCompilationUnitA =
        (ASTCCompilationUnitA) componentSymbol.get()
            .getEnclosingScope()
            .getAstNode();
    ASTLanguageComponentCompilationUnit componentCompUnit = astcCompilationUnitA.getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit();

    Optional<ASTLanguageComponentCompilationUnit> customizedLanguageComponent =
        resolver.resolveCustomization(
            componentCompUnit,
            cc.get().getAstNode());
    assertFalse(customizedLanguageComponent.isPresent());
  }

  
  protected static class TestArtifactComposer extends AbstractArtifactComposer {
    private ASTLanguageComponentCompilationUnit epComp;
    private ASTLanguageComponentCompilationUnit ppComp;

    public TestArtifactComposer(MCPath modelPath, Path outputPath) {
      super(modelPath, outputPath);
    }

    @Override
    public void compose(ASTLanguageComponentCompilationUnit ppComponent,
                        ASTLanguageComponentCompilationUnit epComponent,
                        Collection<Binding> bindings) {
      this.epComp = epComponent;
      this.ppComp = ppComponent;

    }

    @Override
    public void aggregate(ASTLanguageComponentCompilationUnit ppComponent, ASTLanguageComponentCompilationUnit epComponent,
                        Collection<Binding> bindings) {
      this.epComp = epComponent;
      this.ppComp = ppComponent;

    }

    @Override
    public void outputResult(String fcName) {

    }

    @Override
    public void addSelectedWfrSets(ASTLanguageComponent rootComponent, List<String> wfrSetNames) {

    }

    @Override
    public void setParameter(ASTLanguageComponent lc, ASTParameter param, String value) {
      // TODO Auto-generated method stub

    }

    @Override
    public String getComposedGrammarName() {
      return getComposedGrammarName(
          epComp.getLanguageComponent().getGrammarDefinition().getMCQualifiedName(),
          ppComp.getLanguageComponent().getGrammarDefinition().getMCQualifiedName()).toString();
    }

    protected ASTMCQualifiedName getComposedGrammarName(
            ASTMCQualifiedName epAS,
            ASTMCQualifiedName ppAS) {

      ASTMCQualifiedName newASName = epAS.deepClone();
      final boolean asMatches = epAS.toString().equals(ppAS.toString());
      final boolean epGrammarContainsPpGrammar = epAS.getPartsList()
          .stream()
          .anyMatch(part -> part.contains(Names.getSimpleName(ppAS.getPartsList())));

      if (!asMatches && !epGrammarContainsPpGrammar) {
        final int newASNameIndex = epAS.getPartsList().size() - 1;
        String newASNameSuffix = epAS.getPartsList().get(newASNameIndex);
        final int ppNameIndex = ppAS.getPartsList().size() - 1;
        newASNameSuffix += "With" + ppAS.getParts(ppNameIndex);
        newASName.setParts(newASNameIndex, newASNameSuffix);
      }
      return newASName;
    }

    @Override
    public String getGeneratorName() {
      return getComposedGrammarName() + "Gen";
    }

    @Override
    public String getGeneratorDomainModelName() {
      String composedComponentName = epComp.getLanguageComponent().getName() + "With" + ppComp.getLanguageComponent().getName();
      return composedComponentName + "domain";
    }
  }
}
