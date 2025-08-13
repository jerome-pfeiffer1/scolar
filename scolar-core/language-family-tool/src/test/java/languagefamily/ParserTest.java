package languagefamily;

import languagefamily._ast.ASTLanguageFamilyCompilationUnit;
import languagefamily._ast.ASTLanguageFamilyCompilationUnitA;
import languagefamily._parser.LanguageFamilyParser;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class ParserTest {

  private static final String MODELPATH =
      "src"
          + File.separator + "test"
          + File.separator + "resources"
          + File.separator + "sourcemodels"
          + File.separator;

  @Test
  public void testLanguageFamily() {
    LanguageFamilyParser parser = new LanguageFamilyParser();

    try {
      Optional<ASTLanguageFamilyCompilationUnitA> montiarcAS =
          parser.parse(MODELPATH + "general" + File.separator + "montiarcexample" + File.separator + "AutomatenArchitektur.family");
      assertTrue(montiarcAS.isPresent());
      assertTrue(montiarcAS.get().isPresentLanguageFamilyCompilationUnit());

    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
