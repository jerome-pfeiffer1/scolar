package languagefamily.symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._ast.ASTFeatureDeclaration;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class SymboltableTest {

  private static final MCPath MODEL_PATH =
      new MCPath(Paths.get("src/test/resources/sourcemodels"));

  final ILanguageFamilyGlobalScope symTab =
          new LanguageFamilyGlobalScope(MODEL_PATH, ".*");

  @Before
  public void setup() {

    LanguageFamilyMill.reset();
    LanguageFamilyMill.init();

    Log.enableFailQuick(false);
  }

  @Test
  public void testLanguageFamilySymbolResolving() {

    final Optional<LanguageFamilySymbol> resolvedLF =
        symTab.resolveLanguageFamily("general.montiarcexample.AutomatenArchitektur");

    assertTrue(resolvedLF.isPresent());
  }

  @Test
  public void testLanguageFamilyProcessorResolvingWithoutCoCos() {
    LanguageFamilyProcessor processor = new LanguageFamilyProcessor(MODEL_PATH);

    final Optional<LanguageFamilySymbol> resolvedLF =
            processor.loadLanguageFamilySymbolWithoutCoCos("general.montiarcexample.AutomatenArchitektur");

    assertTrue(resolvedLF.isPresent());
  }

  @Test
  public void testLanguageComponentResolving() {
    final Optional<LanguageFamilySymbol> resolvedLF =
            symTab.resolveLanguageFamily("general.montiarcexample.AutomatenArchitektur");

    assertTrue(resolvedLF.isPresent());
    List<ASTFeatureDeclaration> featuresList = resolvedLF.get().getAstNode().getFeaturesList();
    for (ASTFeatureDeclaration astFeatureDeclaration : featuresList) {
      ASTMCQualifiedName realizingComponentName = astFeatureDeclaration.getRealizingComponentName();
      Optional<LanguageComponentSymbol> languageComponentSymbol = symTab.resolveLanguageComponent(realizingComponentName.getQName());
      assertTrue(languageComponentSymbol.isPresent());
    }
  }
}
