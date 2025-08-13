package metacomposition;

import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.*;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import org.junit.Before;

import composition.AbstractArtifactComposer;
import util.Binding;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public abstract class AbstractComposerTest {

  public static final MCPath MODEL_PATH = new MCPath(
      Paths.get("src/test/resources/composition/"),
      Paths.get("src", "test", "resources"),
      Paths.get("src/test/resources/sourcemodels/")
//      Paths.get("target/test-artifacts/resources/sourcemodels/general/montiarcexample/")
      );

  protected LanguageComponentBaseProcessor processor;

  protected BaseLanguageComponentComposer composer;

  @Before
  public void setUp() {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
    LanguageComponentBaseMill.reset();
    LanguageComponentBaseMill.init();

    processor = new LanguageComponentBaseProcessor(
        MODEL_PATH);
    composer = new BaseLanguageComponentComposer(
        new TestArtifactComposer(null, null), processor);
  }

  /**
   *
   * @param languageComponent The language component to check
   * @param expectedEPs
   * @param expectedPPs
   * @param expectedWFRSets
   * @param expectedComposedComponentName
   */
  protected void checkCorrectnessOfLanguageComponent(
      ASTLanguageComponent languageComponent,
      Map<String, ASTOptionality> expectedEPs,
      Collection<String> expectedPPs,
      Collection<String> expectedWFRSets,
      List<String> expectedASReferenceList,
      String expectedComposedComponentName) {

    assertEquals(expectedComposedComponentName, languageComponent.getName());

    assertEquals(new HashSet<>(expectedASReferenceList), new HashSet<>(languageComponent.getASReferences()));

    // ===== Extension Points =====
    Collection<ASTRequiredExtension> extensionPoints =
        languageComponent.getExtensionPoints();
    assertEquals(expectedEPs.keySet().size(), extensionPoints.size());


    final Set<String> existingEPNames =
        extensionPoints.stream()
            .map(ASTRequiredExtension::getName)
            .collect(Collectors.toSet());
    for (ASTRequiredExtension extensionPoint : extensionPoints) {
      assertTrue(expectedEPs.containsKey(extensionPoint.getName()));
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

  /**
   * Load the language component with the given name. Asserts the component is present.
   * @param qualifiedName Name of component to load
   * @param checkCoCos
   * @return The loaded component.
   */
  protected ASTLanguageComponent loadLanguageComponent(String qualifiedName, boolean checkCoCos) {
    return loadLanguageComponentSymbol(qualifiedName, checkCoCos).getAstNode();
  }

  protected LanguageComponentSymbol loadLanguageComponentSymbol(String qualifiedName, boolean checkCoCos) {
    final Optional<LanguageComponentSymbol> component;
    if(checkCoCos) {
      component = processor.loadLanguageComponentSymbol(qualifiedName);
    } else {
      component = processor.loadLanguageComponentSymbolWithoutCoCos(qualifiedName);
    }
    assertTrue("Language component " + qualifiedName + " could not be loaded.",
            component.isPresent());
    assertTrue(component.get().isPresentAstNode());
    return component.get();
  }


    protected ASTLanguageComponentCompilationUnit composeLanguageComponent(
      String ppComponent,
      String epComponent,
      List<Binding> bindings,
      boolean checkCoCos) {

    final Optional<LanguageComponentSymbol> ppComp;
    if(checkCoCos) {
      ppComp = processor.loadLanguageComponentSymbol(ppComponent);
    } else {
      ppComp = processor.loadLanguageComponentSymbolWithoutCoCos(ppComponent);
    }
    assertTrue(ppComp.isPresent());
    assertTrue(ppComp.get().isPresentAstNode());

    final Optional<LanguageComponentSymbol> epComp;
    if(checkCoCos) {
      epComp = processor.loadLanguageComponentSymbol(epComponent);
    } else {
      epComp = processor.loadLanguageComponentSymbolWithoutCoCos(epComponent);
    }
    assertTrue(epComp.isPresent());
    assertTrue(epComp.get().isPresentAstNode());
      ASTLanguageComponentCompilationUnitA epAST = (ASTLanguageComponentCompilationUnitA) epComp.get().getEnclosingScope().getAstNode();
      ASTLanguageComponentCompilationUnitA ppAST = (ASTLanguageComponentCompilationUnitA) ppComp.get().getEnclosingScope().getAstNode();

    ASTLanguageComponentCompilationUnit composedComponent =
        composer.composeLanguageComponent(
            ppAST.getLanguageComponentCompilationUnit(),
            epAST.getLanguageComponentCompilationUnit(),
            bindings);

    System.out.println(LanguageComponentBaseMill.prettyPrint(composedComponent, true));

    return composedComponent;
  }

  private class TestArtifactComposer extends AbstractArtifactComposer {

    private ASTLanguageComponent epComp;
    private ASTLanguageComponent ppComp;

    /**
     * Constructor for metacomposition.TestArtifactComposer
     * @param modelPath
     * @param outputPath
     */
    public TestArtifactComposer(MCPath modelPath, Path outputPath) {
      super(modelPath, outputPath);
    }

    @Override
    public void addSelectedWfrSets(ASTLanguageComponent rootComponent, List<String> wfrSetNames) {
      // Intentionally left unimplemented
    }

    @Override
    public void compose(ASTLanguageComponentCompilationUnit ppComponent, ASTLanguageComponentCompilationUnit epComponent,
                        Collection<Binding> bindings) {
      this.epComp = epComponent.getLanguageComponent();
      this.ppComp = ppComponent.getLanguageComponent();

          }

    @Override
    public void aggregate(ASTLanguageComponentCompilationUnit ppComponent, ASTLanguageComponentCompilationUnit epComponent,
                          Collection<Binding> bindings) {
      this.epComp = epComponent.getLanguageComponent();
      this.ppComp = ppComponent.getLanguageComponent();

    }

    @Override
    public void setParameter(ASTLanguageComponent lc, ASTParameter param, String value) {
      // Intentionally left unimplemented
    }

    @Override
    public void outputResult(String fcName) {
      // Intentionally left unimplemented
    }

    /**
     * @see AbstractArtifactComposer#getComposedGrammarName()
     */
    @Override
    public String getComposedGrammarName() {
      return getComposedGrammarName(
          epComp.getGrammarDefinition().getMCQualifiedName(),
          ppComp.getGrammarDefinition().getMCQualifiedName()).toString();
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

    /**
     * @see AbstractArtifactComposer#getGeneratorName()
     */
    @Override
    public String getGeneratorName() {
      return getComposedGrammarName() + "Gen";
    }

    /**
     * @see AbstractArtifactComposer#getGeneratorDomainModelName()
     */
    @Override
    public String getGeneratorDomainModelName() {
      String composedComponentName = epComp.getName() + "With" + ppComp.getName();
      return composedComponentName + "domain";
    }


  }
}
