package metacomposition;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import languagecomponentbase._ast.*;
import org.junit.Before;

import composition.AbstractArtifactComposer;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import junit.framework.TestCase;
import languagecomponentbase.LanguageComponentBaseProcessor;
import util.Binding;

public abstract class AbstractComposerTest {
  
  public static final Path PATH = Paths.get("src/test/resources/composition/");
  
  public static final Path OUTPUT_PATH = Paths.get("target/generated-test-models");
  
  protected LanguageComponentBaseProcessor processor;
  
  protected BaseLanguageComponentComposer composer;
  protected MCPath modelPath = new MCPath(PATH);
  
  @Before
  public void setUp() {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
    
    processor = new LanguageComponentBaseProcessor(modelPath);
    composer = new BaseLanguageComponentComposer(new TestArtifactComposer(modelPath, OUTPUT_PATH), processor);
  }
  
  /**
   * Checks whether the given language component fits the expected
   * characteristics.
   *
   * @param languageComponent Language component to check
   * @param expectedEPs EPs that are expected to be present in the language
   * component
   * @param expectedPPs PPs that are expected to be present in the language
   * component
   * @param expectedWFRSets
   * @param expectedASReference First abstract syntax reference that is expected
   * to be present
   * @param expectedComposedComponentName Expected name of the language
   * component
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
        languageComponent.getGrammarDefinition().getMCQualifiedName().getQName());
    
    // ===== Extension Points =====
    Collection<ASTRequiredExtension> extensionPoints = languageComponent.getExtensionPoints();
    assertEquals(expectedEPs.keySet().size(), extensionPoints.size());
    
    for (ASTRequiredExtension extensionPoint : extensionPoints) {
      TestCase.assertTrue(expectedEPs.containsKey(extensionPoint.getName()));
      ASTOptionality expectedOptionality = expectedEPs.get(extensionPoint.getName());
      assertEquals(
          "Expected that EP:" + extensionPoint.getName() + " is " + expectedOptionality
              + " but was " + extensionPoint.getOptionality(),
          expectedOptionality, extensionPoint.getOptionality());
    }
    
    // ===== Provision Points =====
    Collection<ASTProvidedExtension> provisionPoints = languageComponent.getProvisionPoints();
    assertEquals(expectedPPs.size(), provisionPoints.size());
    
    final Set<String> existingPPNames = provisionPoints.stream()
        .map(ASTProvidedExtension::getName)
        .collect(Collectors.toSet());
    
    // Check all expected present
    for (ASTProvidedExtension provisionPoint : provisionPoints) {
      assertTrue(expectedPPs.contains(provisionPoint.getName()));
    }
    // Check no more than expected present
    for (final String existingPPName : existingPPNames) {
      assertTrue(expectedPPs.contains(existingPPName));
    }
    
    // ===== WFR Sets ======
    final Collection<ASTWfrSetDefinition> existingWFRSets = languageComponent.getWfrSetDefinitions();
    assertEquals(expectedWFRSets.size(), existingWFRSets.size());
    final Set<String> existingWFRSetNames = existingWFRSets.stream()
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
  
  
  protected class TestArtifactComposer extends AbstractArtifactComposer {
    private ASTLanguageComponentCompilationUnit epComp;
    private ASTLanguageComponentCompilationUnit ppComp;
    
    public TestArtifactComposer(MCPath modelPath, Path outputPath) {
      super(modelPath, outputPath);
    }
    
    @Override
    public void compose(ASTLanguageComponentCompilationUnit ppComponent, ASTLanguageComponentCompilationUnit epComponent,
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
      String composedComponentName = epComp.getLanguageComponent().getName() + "With" + ppComp.getLanguageComponent().getName();
      return composedComponentName + "domain";
    }
  }
}
