package customizationconfiguration;

import customizationconfiguration._ast.ASTCCCompilationUnit;
import customizationconfiguration._ast.ASTCCompilationUnitA;
import customizationconfiguration._parser.CustomizationConfigurationParser;
import customizationconfiguration._symboltable.CustomizationConfigurationSymbol;
import de.monticore.io.paths.MCPath;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class ParserTest {

  private static final String MODELPATH =
      "src"
          + File.separator + "test"
          + File.separator + "resources"
          + File.separator;

  @Test
  public void testCustomizationConfiguration() {
    CustomizationConfigurationParser ccParser = new CustomizationConfigurationParser();
    try {
      final Optional<ASTCCompilationUnitA> parse =
          ccParser.parse(MODELPATH + "general" + File.separator + "montiarcexample" + File.separator+ "customizationconfiguration" + File.separator + "MAA.cc");
      assertTrue(parse.isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCustomizationProcessor() {
    MCPath modelPath = new MCPath(Paths.get(MODELPATH)
    );
    final String ccName = "general.montiarcexample.customizationconfiguration.MAA";

    CustomizationConfigurationProcessor customizationConfigurationProcessor = new CustomizationConfigurationProcessor(modelPath);
    Optional<CustomizationConfigurationSymbol> customizationConfigurationSymbol = customizationConfigurationProcessor.loadCCSymbol(ccName);
    assertTrue(customizationConfigurationSymbol.isPresent());


  }
}
