package cocos;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentBaseNode;
import languagecomponentbase._cocos.LanguageComponentBaseCoCoChecker;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagecomponentbase.cocos.LanguageComponentBaseCoCos;

import org.junit.Before;

import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class AbstractCoCoTest {
  public static final boolean ENABLE_FAIL_QUICK = false;
  protected static final MCPath MODEL_PATH =
      new MCPath(
          Paths.get("src/test/resources/"),
          Paths.get("src/test/resources/sourcemodels/")
      );
  protected static final LanguageComponentBaseProcessor LanguageComponent_PROCESSOR =
      new LanguageComponentBaseProcessor(MODEL_PATH);
  protected static final String INVALID_MODEL_PACKAGE = "cocos.languageComponents.invalid";

  @Before
  public void setup() {
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
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
    LanguageComponentBaseCoCos.createChecker().checkAll(node);

    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }

  protected static void checkInvalid(LanguageComponentBaseCoCoChecker cocos, ASTLanguageComponentBaseNode node,
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
}
