package statechartexample;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase.MCLanguageComponentProcessor;
import languagecomponentbase._symboltable.*;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._symboltable.*;

import implication.ImplicationCalculator;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ComponentCorrectnessTest {

  private static final MCPath MODEL_PATH = new MCPath(
          Paths.get("src/main/resources/"),
          Paths.get("target/generated-sources/monticore/sourcecode")
  );
  protected static final Path OUTPUT_PATH =
      Paths.get("target/test-results/statechart/implications");

  @Before
  public void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testFinalState() {
    final String modelname = "sc.finalstate.FinalState";
    final ASTLanguageComponent finalState =
        loadLanguageComponentAST(modelname);

    //IMPLICATION_CALCULATOR.calculateImplications(modelname, MODEL_PATH, OUTPUT_PATH);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testChar() {
    final String modelname = "sc.charguard.CharGuard";
    final ASTLanguageComponent charGuard =
        loadLanguageComponentAST(modelname);

    //IMPLICATION_CALCULATOR.calculateImplications(modelname, MODEL_PATH, OUTPUT_PATH);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testStatechart() {
    final String modelname = "sc.StateChart";
    final ASTLanguageComponent statechart =
        loadLanguageComponentAST(modelname);

    //IMPLICATION_CALCULATOR.calculateImplications(modelname, MODEL_PATH, OUTPUT_PATH);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testJavaExpressions() {
    final String modelname = "sc.jexpr.JavaExpressions";
    final ASTLanguageComponent finalstate =
        loadLanguageComponentAST(modelname);

    //IMPLICATION_CALCULATOR.calculateImplications(modelname, MODEL_PATH, OUTPUT_PATH);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAutomaton() {
    final String modelname = "aut.Automaton";
    final ASTLanguageComponent automaton = loadLanguageComponentAST(modelname);

    assertEquals(0, Log.getErrorCount());
  }
  @Test
  public void testCD4An() {
    final String modelname = "aut.CD4An";
    final ASTLanguageComponent cd4an = loadLanguageComponentAST(modelname);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testStateChartsFamily() {
    LanguageFamilyMill.reset();
    LanguageFamilyMill.init();
    LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH);

    final Optional<LanguageFamilySymbol> languageFamilySymbol =
            familyTool.loadLanguageFamilySymbol("sc.StateChartsFamily");

    assertTrue(languageFamilySymbol.isPresent());
    assertEquals(0, Log.getErrorCount());
  }

  protected ASTLanguageComponent loadLanguageComponentAST(String qualifiedName) {
    LanguageComponentBaseMill.reset();
    LanguageComponentBaseMill.init();
    final MCLanguageComponentProcessor componentProcessor = new MCLanguageComponentProcessor(MODEL_PATH);

    final Optional<LanguageComponentSymbol> languageComponentSymbol = componentProcessor.loadLanguageComponentSymbol(qualifiedName);

    assertTrue(languageComponentSymbol.isPresent());
    assertTrue(languageComponentSymbol.get().getAstNode().isPresentSymbol());
    assertTrue(languageComponentSymbol.get().getAstNode() instanceof ASTLanguageComponent);
    return languageComponentSymbol.get().getAstNode();
  }
}
