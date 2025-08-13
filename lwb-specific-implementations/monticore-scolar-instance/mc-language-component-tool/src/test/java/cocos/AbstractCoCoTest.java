package cocos;

import static junit.framework.TestCase.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import languagecomponentbase.LanguageComponentBaseMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._ast.ASTLanguageFamilyNode;
import languagefamily._cocos.LanguageFamilyCoCoChecker;
import languagefamily._symboltable.LanguageFamilySymbol;
import org.junit.Before;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase.cocos.MCLanguageComponentBaseCoCos;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponentBaseNode;
import languagecomponentbase._cocos.LanguageComponentBaseCoCoChecker;
import languagecomponentbase._symboltable.LanguageComponentSymbol;

public class AbstractCoCoTest {
  public static final boolean ENABLE_FAIL_QUICK = false;
  protected static final MCPath MODEL_PATH =
      new MCPath(
          Paths.get("src/test/resources/"),
          Paths.get("src/test/resources/grammars/")
      );
  protected static final LanguageComponentBaseProcessor LanguageComponent_PROCESSOR =
      new LanguageComponentBaseProcessor(MODEL_PATH);

  protected static final LanguageFamilyProcessor LanguageFamily_PROCESSOR = new LanguageFamilyProcessor(MODEL_PATH);

  @Before
  public void setup() {
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
    ExpectedErrorInfo.setERROR_CODE_PATTERN(Pattern.compile("MC[0-9]{3}"));
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
    // reset global scope
    Grammar_WithConceptsMill.globalScope().clear();
    Grammar_WithConceptsMill.globalScope().setFileExt("mc4");
    Grammar_WithConceptsMill.globalScope().setSymbolPath(MODEL_PATH);
    LanguageComponentBaseMill.reset();
    LanguageComponentBaseMill.init();
  }

  protected ASTLanguageComponent loadLanguageComponentAST(String qualifiedName) {
    final Optional<LanguageComponentSymbol> languageComponentSymbol =
            LanguageComponent_PROCESSOR.loadLanguageComponentSymbolWithoutCoCos(qualifiedName);
    assertTrue(languageComponentSymbol.isPresent());
    assertTrue(languageComponentSymbol.get().isPresentAstNode());
    assertTrue(languageComponentSymbol.get().getAstNode() instanceof ASTLanguageComponent);
    return (ASTLanguageComponent) languageComponentSymbol.get().getAstNode();
  }


  protected void checkValid(String packageName, String modelName) {
    final String qualifiedName = packageName + "." + modelName;
    ASTLanguageComponent node = loadLanguageComponentAST(qualifiedName);
    Log.getFindings().clear();
    MCLanguageComponentBaseCoCos.createChecker().checkAll(node);
    System.out.println(Log.getFindings());
    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }

  protected static void checkInvalid(LanguageComponentBaseCoCoChecker cocos,
                                     ASTLanguageComponentBaseNode node,
                                     ExpectedErrorInfo expectedErrors) {

    // check whether all the expected errors are present when using all cocos
    Log.getFindings().clear();
    cocos.checkAll(node);
    System.out.println(Log.getFindings());
    expectedErrors.checkOnlyExpectedPresent(
        Log.getFindings(),
        "Got no findings when checking only "
            + "the given coco. Did you pass an empty coco checker?");
  }

  protected void checkInvalid(
      LanguageComponentBaseCoCoChecker cocos,
      String modelName,
      ExpectedErrorInfo expectedErrors){

    final ASTLanguageComponent astLanguageComponent = loadLanguageComponentAST(modelName);
    AbstractCoCoTest.checkInvalid(cocos, astLanguageComponent, expectedErrors);
  }
/*
  protected static void checkInvalid(LanguageFamilyCoCoChecker cocos,
                                     ASTLanguageFamilyNode node,
                                     ExpectedErrorInfo expectedErrors) {

    // check whether all the expected errors are present when using all cocos
    Log.getFindings().clear();
    cocos.checkAll(node);
    System.out.println(Log.getFindings());
    expectedErrors.checkOnlyExpectedPresent(
            Log.getFindings(),
            "Got no findings when checking only "
                    + "the given coco. Did you pass an empty coco checker?");
  }

  protected void checkInvalid(
          LanguageFamilyCoCoChecker cocos,
          String modelName,
          ExpectedErrorInfo expectedErrors){

    final ASTLanguageFamily astLanguageFamily = loadLanguageFamilyAST(modelName); // correct?
    AbstractCoCoTest.checkInvalid(cocos, astLanguageFamily, expectedErrors);
  }

  protected ASTLanguageFamily loadLanguageFamilyAST(String qualifiedName) {
    final Optional<LanguageFamilySymbol> languageFamilySymbol =
            LanguageFamily_PROCESSOR.loadLanguageFamilySymbolWithoutCoCos(qualifiedName);
    assertTrue(languageFamilySymbol.isPresent());
    assertTrue(languageFamilySymbol.get().isPresentAstNode());
    assertTrue(languageFamilySymbol.get().getAstNode() instanceof ASTLanguageFamily);
    return (ASTLanguageFamily) languageFamilySymbol.get().getAstNode();
  }
*/
}
