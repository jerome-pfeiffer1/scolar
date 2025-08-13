package customizationconfiguration.cocos;

import customizationconfiguration.CustomizationConfigurationProcessor;
import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._ast.ASTCustomizationConfigurationNode;
import customizationconfiguration._cocos.CustomizationConfigurationCoCoChecker;
import customizationconfiguration._symboltable.CustomizationConfigurationSymbol;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import junit.framework.TestCase;
import org.junit.Before;

import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public abstract class AbstractCoCoTest {


  protected static final MCPath MODEL_PATH = new MCPath(
  Paths.get("src/test/resources/"),
  Paths.get("target/test-artifacts/resources/sourcemodels"));

  protected static void checkInvalid(
      CustomizationConfigurationCoCoChecker cocos, ASTCustomizationConfigurationNode node,
      ExpectedErrorInfo expectedErrors) {

    // check whether all the expected errors are present when using all cocos
    Log.getFindings().clear();
    cocos.checkAll(node);
    expectedErrors.checkOnlyExpectedPresent(Log.getFindings(),
        "Got no findings when checking only "
            + "the given coco. Did you pass an empty coco checker?");
  }

  @Before
  public void setUp() throws Exception {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
  }

  protected ASTCustomizationConfiguration getAstCustomizationConfiguration(String name) {
    CustomizationConfigurationProcessor tool =
        new CustomizationConfigurationProcessor(
            MODEL_PATH);

    final Optional<CustomizationConfigurationSymbol> f =
        tool.loadCCSymbolWithoutCoCos(name);
    assertTrue(f.isPresent());
    TestCase.assertTrue(f.get().isPresentAstNode());

    return f.get().getAstNode();
  }

  private void checkConfigurationValid(String packageName, String modelName) {
    final String name = packageName + "." + modelName;
    final ASTCustomizationConfiguration configuration = getAstCustomizationConfiguration(name);

    CustomizationConfigurationCoCos.createChecker().checkAll(configuration);

    new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
  }
}
