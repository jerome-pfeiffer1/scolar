import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._prettyprint.LanguageComponentBasePrettyPrinter;

import org.junit.Before;
import org.junit.Test;

import de.monticore.prettyprint.IndentPrinter;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._parser.LanguageComponentBaseParser;

public class PrettyPrinterTest {
  
  private static final String MODELPATH = "src" + File.separator + "test"
      + File.separator + "resources"
      + File.separator + "sourcemodels"
      + File.separator + "general"
      + File.separator + "montiarcexample"
      + File.separator;

  @Before
  public void setup() {
    Log.enableFailQuick(false);
    Log.init();
    LanguageComponentBaseMill.reset();
    LanguageComponentBaseMill.init();
  }

  @Test
  public void testLanguageComponentPrettyPrinterOnMontiArcComponent() {
    LanguageComponentBaseParser parser = new LanguageComponentBaseParser();
    
    try {
      Optional<ASTLanguageComponentCompilationUnitA> montiArcLanguageComponent = parser
          .parse(MODELPATH + "montiarc" + File.separator + "MontiArc.comp");
      assertTrue(montiArcLanguageComponent.isPresent());

      String prettyPrint = LanguageComponentBaseMill.prettyPrint(montiArcLanguageComponent.get().getLanguageComponentCompilationUnit(), true);
      System.out.print(prettyPrint);
      assertTrue(parser.parse_String(prettyPrint).isPresent());
      
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testPrettyPrinterTestComponent() {
    LanguageComponentBaseParser parser = new LanguageComponentBaseParser();

    try {
      Optional<ASTLanguageComponentCompilationUnitA> prettyPrinterTestComponent =
          parser.parse("src" + File.separator
              + "test" + File.separator
              + "resources" + File.separator
              + "prettyprinter" + File.separator
              + "PrettyPrinterTestComponent.comp");
      assertTrue(prettyPrinterTestComponent.isPresent());


      String prettyPrint = LanguageComponentBaseMill.prettyPrint(prettyPrinterTestComponent.get().getLanguageComponentCompilationUnit(), true);
      System.out.print(prettyPrint);
      assertTrue(parser.parse_String(prettyPrint).isPresent());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
