package languagefamily;

import de.se_rwth.commons.logging.Log;
import languagefamily._ast.ASTLanguageFamilyCompilationUnitA;
import languagefamily._parser.LanguageFamilyParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrettyPrinterTest {
  
  @Before
  public void setUp() throws Exception {
    Log.enableFailQuick(false);
    LanguageFamilyMill.reset();
    LanguageFamilyMill.init();
  }
  
  private static final String MODELPATH = "src"
      + File.separator + "test"
      + File.separator + "resources"
      + File.separator + "sourcemodels"
      + File.separator;
  
  @Test
  public void testLanguageFamilyPrettyPrinter() {
    LanguageFamilyParser parser = new LanguageFamilyParser();
    
    try {
      Optional<ASTLanguageFamilyCompilationUnitA> family = parser.parse(MODELPATH + "general"
          + File.separator + "montiarcexample" + File.separator + "AutomatenArchitektur.family");
      assertTrue(family.isPresent());

      String result = LanguageFamilyMill.prettyPrint(family.get(),true);
      System.out.println(result);
      Optional<ASTLanguageFamilyCompilationUnitA> parseResult = parser.parse_String(result);
      assertTrue(parseResult.isPresent());
      
      assertEquals(6, parseResult.get().getLanguageFamilyCompilationUnit().getLanguageFamily().getFeaturesList().size());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
