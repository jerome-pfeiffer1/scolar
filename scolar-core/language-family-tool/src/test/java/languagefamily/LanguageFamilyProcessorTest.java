package languagefamily;


import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTOptionality;
import languagecomponentbase._ast.ASTRequiredExtension;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._ast.ASTLanguageFamilyCompilationUnit;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;
import metacomposition.AbstractComposerTest;
import metacomposition.BaseLanguageComponentComposer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Michael Mutert
 */
public class LanguageFamilyProcessorTest extends AbstractComposerTest{

  public static final MCPath MODEL_PATH =
      new MCPath(Paths.get("src/test/resources"),
          Paths.get("src", "test", "resources", "sourcemodels"),  OUTPUT_PATH);

  private ILanguageFamilyGlobalScope symbolTable = new LanguageFamilyGlobalScope(MODEL_PATH, ".*");
  private LanguageFamilyProcessor lfProcessor = new LanguageFamilyProcessor(MODEL_PATH);
  private LanguageFamilyResolver lfResolver;

  @Before
  @Override
  public void setUp() {
    super.setUp();
    LanguageFamilyMill.reset();
    LanguageFamilyMill.init();

    Log.getFindings().clear();
    processor = new LanguageComponentBaseProcessor(symbolTable);
    composer = new BaseLanguageComponentComposer(
            new TestArtifactComposer(MODEL_PATH, OUTPUT_PATH),
            processor);
    lfResolver = new LanguageFamilyResolver(composer, MODEL_PATH, OUTPUT_PATH);

  }

  @Test
  public void configureLanguageFamily() throws IOException {

    final String languageFamilyName = "AutomatenArchitektur";
    final String languageFamilyFQName = "general.montiarcexample." + languageFamilyName;
    final String featureConfigFileName =
        "src/test/resources/sourcemodels/general/montiarcexample/MAA.conf";

    // Load the language family and variation interface configuration
    Optional<LanguageFamilySymbol> family =
            lfProcessor.loadLanguageFamilySymbolWithoutCoCos(languageFamilyFQName);
    assertTrue("Could not load the initial LC.", family.isPresent());
    assertTrue("Symbol of initial LC has no AST node.",
        family.get().isPresentAstNode());

    Optional<ASTFCCompilationUnit> fc =
        new FeatureConfigurationParser().parse(featureConfigFileName);
    assertTrue("Could not load the feature configuration.", fc.isPresent());

    // resolve variation interface with loaded config
    Optional<ASTLanguageComponentCompilationUnit> composedLanguageComponent =
        lfResolver.configureLanguageFamily(
            family.get().getAstNode(),
            fc.get().getFeatureConfiguration());

    assertTrue(
        "The process returned an empty result.",
        composedLanguageComponent.isPresent()
    );

//    LanguageFamilyMill.prettyPrint(composedLanguageComponent.get(), true);
//    System.out.println(familyPrettyPrinter.getResult());

    final ASTLanguageComponent languageComponent =
        composedLanguageComponent.get().getLanguageComponent();
    assertEquals(
        "The name of the generated Language Component is incorrect.",
        "MontiArcWithConnectorCorrectnessWithSCWithSCCorrectWithVar", languageComponent.getName());

    // Check CI of generated LC
    assertEquals(
        "The number of extension points in the generated CI does " +
            "not match the expected amount.",
        7, languageComponent.getExtensionPoints().size());


    assertEquals(
        "The number of parameters in the generated CI does not match the " +
            "expected number.",
        2, languageComponent.getParameters().size());


    // Correct grammar
    final String actualGrammarName =
        languageComponent.getGrammarDefinition().getMCQualifiedName().toString();
    assertEquals("mc.lang.MontiArcWithStateChartsWithVariable", actualGrammarName);

    // Check: AS EPs aus dem CI sind alle vorhanden
    checkExtensionPoint("Verhalten", ASTOptionality.OPTIONAL, languageComponent);
    checkExtensionPoint("Invariante", ASTOptionality.OPTIONAL, languageComponent);
    checkExtensionPoint("Var", ASTOptionality.OPTIONAL, languageComponent);

    // AS EP aus dem AS sind alle im CI vorhanden

    // Transformation
    final Optional<ASTRequiredExtension> invariante2Java =
        languageComponent.getExtensionPoint("Invariante2Java");
    assertTrue(invariante2Java.isPresent());

    final Optional<ASTRequiredExtension> scElementTrafo =
        languageComponent.getExtensionPoint("SCElementTrafo");
    assertTrue(scElementTrafo.isPresent());

    final Optional<ASTRequiredExtension> varTrafo =
        languageComponent.getExtensionPoint("VarTrafo");
    assertTrue(varTrafo.isPresent());
  }

  /**
   * Ensures that there is an extension point in the language component
   * with the given name and optionality.
   * @param name
   * @param expectedOptionality
   * @param languageComponent
   */
  private void checkExtensionPoint(
      String name,
      ASTOptionality expectedOptionality,
      ASTLanguageComponent languageComponent) {

    final Optional<ASTRequiredExtension> extensionPoint =
        languageComponent.getExtensionPoint(name);
    assertTrue(extensionPoint.isPresent());
    assertEquals(expectedOptionality, extensionPoint.get().getOptionality());
  }

  @Test
  public void loadLanguageFamilySymbol() {
    final String modelName = "general.montiarcexample.AutomatenArchitektur";
    final Optional<LanguageFamilySymbol> languageFamilySymbol =
        lfProcessor.loadLanguageFamilySymbol(modelName);
    assertTrue(languageFamilySymbol.isPresent());
    assertTrue(languageFamilySymbol.get().isPresentAstNode());
    assertTrue(languageFamilySymbol.get().getAstNode() instanceof ASTLanguageFamily);
  }

  @Test
  public void loadCompilationUnit() {
    final String modelName = "general.montiarcexample.AutomatenArchitektur";
    final Optional<ASTLanguageFamilyCompilationUnit> compilationUnit =
        lfProcessor.loadCompilationUnit(modelName);
    assertTrue(compilationUnit.isPresent());
    final ASTLanguageFamily languageFamily = compilationUnit.get().getLanguageFamily();
    assertTrue(languageFamily.isPresentSymbol());
    final LanguageFamilySymbol symbol = languageFamily.getSymbol();
    assertEquals(symbol.getAstNode(), languageFamily);
  }

  @Test
  public void loadLanguageFamilySymbolWithoutCoCos() {
    final String modelName = "general.montiarcexample.AutomatenArchitektur";
    final Optional<LanguageFamilySymbol> languageFamilySymbol =
        lfProcessor.loadLanguageFamilySymbolWithoutCoCos(modelName);
    assertTrue(languageFamilySymbol.isPresent());
    assertTrue(languageFamilySymbol.get().isPresentAstNode());
  }
}
