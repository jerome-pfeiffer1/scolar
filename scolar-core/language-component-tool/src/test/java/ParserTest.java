import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import org.junit.Test;

import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._parser.LanguageComponentBaseParser;


public class ParserTest {

  private static final String MODELPATH =
      "src" + File.separator + "test"
          + File.separator + "resources"
          + File.separator + "sourcemodels"
          + File.separator;

  @Test
  public void testLanguageComponent() {
    LanguageComponentBaseParser parser = new LanguageComponentBaseParser();

    try {
      Optional<ASTLanguageComponentCompilationUnitA> montiarcAS =
          parser.parse(
              MODELPATH
                  + "general" + File.separator
                  + "montiarcexample" + File.separator
                  + "montiarc" + File.separator + "MontiArc.comp");
      assertTrue(montiarcAS.isPresent());

      Optional<ASTLanguageComponentCompilationUnitA> scAS =
          parser.parse(MODELPATH
                           + "general" + File.separator
                           + "montiarcexample" + File.separator
                           + "statecharts" + File.separator + "SC.comp");
      assertTrue(scAS.isPresent());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
