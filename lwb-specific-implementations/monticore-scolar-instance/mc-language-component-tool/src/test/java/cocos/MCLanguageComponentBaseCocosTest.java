///*
// * Copyright (c) 2019 RWTH Aachen. All rights reserved.
// *
// * http://www.se-rwth.de/
// */
package cocos;

import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import org.junit.Assert;
import org.junit.Test;

import languagecomponentbase.cocos.MCLanguageComponentBaseCoCos;

/**
// * Tests the Context Condition {@link languagecomponentbase.cocos.ReferencedGrammarExists}
// *
// * @author Jerome Pfeiffer
// * @author Michael Mutert
// */
public class MCLanguageComponentBaseCocosTest extends AbstractCoCoTest {


  @Test
  public void testReferencedGrammarExistsInvalid() {
    ExpectedErrorInfo info = new ExpectedErrorInfo(1, "MC001");
    checkInvalid(MCLanguageComponentBaseCoCos.createChecker(),"cocos.invalid.ReferencedGrammarNotExists", info);
  }

  @Test
  public void testGrammarResolving() {
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
    IGrammar_WithConceptsGlobalScope scope = Grammar_WithConceptsMill.globalScope();
    // reset global scope
    scope.clear();
    scope.setFileExt("mc4");
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/grammars"));

    Optional<MCGrammarSymbol> mcGrammarSymbol = scope.resolveMCGrammar("mc.lang.G1");
    Assert.assertTrue(mcGrammarSymbol.isPresent());
  }

  @Test
  public void testReferencedGrammarExistsValid() {
    checkValid("cocos.valid", "ReferencedGrammarExists");
  }

  @Test
  public void testReferencedRuleInEPExists() {
    ExpectedErrorInfo info = new ExpectedErrorInfo(2, "MC002", "MC004");
    checkInvalid(MCLanguageComponentBaseCoCos.createChecker(), "cocos.invalid.ReferencedRuleInEPDoesNotExist", info);
  }


  @Test
  public void testReferencedRuleInPPDoesNotExist() {
    ExpectedErrorInfo info = new ExpectedErrorInfo(1, "MC003");
    checkInvalid(MCLanguageComponentBaseCoCos.createChecker(),"cocos.invalid.ReferencedRuleInPPDoesNotExist", info);
  }

  @Test
  public void testReferencedRuleInEPNotAnInterface() {
    ExpectedErrorInfo info = new ExpectedErrorInfo(1, "MC004");
    checkInvalid(MCLanguageComponentBaseCoCos.createChecker(),"cocos.invalid.ReferencedRuleInEPNotAnInterface", info);
  }

  @Test
  public void testReferencedProductionsHaveNoRightHandSide() {
    ExpectedErrorInfo info = new ExpectedErrorInfo(3, "MC004", "MC005");
    checkInvalid(MCLanguageComponentBaseCoCos.createChecker(),"cocos.invalid.ReferencedProductionsHaveNoRightHandSide", info);

  }
}
