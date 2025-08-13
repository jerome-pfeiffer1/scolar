package cocos;

import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import languagecomponentbase.cocos.MCLanguageComponentBaseCoCos;
import languagecomponentbase.cocos.MCLanguageFamilyCoCos;
import languagefamily._ast.ASTLanguageFamily;
import org.junit.Assert;
import org.junit.Test;
public class MCLanguageFamilyCoCosTest extends AbstractCoCoTest {


  @Test
  public void testReferencedProductionsHaveNoRightHandSideBeta() {

      //ExpectedErrorInfo info = new ExpectedErrorInfo(3, "MC004", "MC005");
      //checkInvalid(MCLanguageFamilyCoCos.createChecker(),"cocos.invalid.ReferencedProductionsHaveNoRightHandSide", info);

  }
}
