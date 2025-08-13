package aut._cocos;

import java.util.Optional;

import aut.automatongrammarwithcharacterexpression._cocos.AutomatonGrammarWithCharacterExpressionCoCoChecker;

import characterexpression.characterexpression.cocos.CharExprCoCos;
import aut.automaton.cocos.AutomatonCoCos;


public class AutomatonWithCharExprCoCos  {

  public AutomatonGrammarWithCharacterExpressionCoCoChecker createTransitionsCorrectChecker() {

    AutomatonGrammarWithCharacterExpressionCoCoChecker checker = new AutomatonGrammarWithCharacterExpressionCoCoChecker();

    AutomatonCoCos automaton = new AutomatonCoCos();
    checker.addChecker(automaton.createTransitionsCorrectChecker());

    return checker;
  }

  public AutomatonGrammarWithCharacterExpressionCoCoChecker createUpperCaseCharactersChecker() {

    AutomatonGrammarWithCharacterExpressionCoCoChecker checker = new AutomatonGrammarWithCharacterExpressionCoCoChecker();

    CharExprCoCos charexpr = new CharExprCoCos();
    checker.addChecker(charexpr.createUpperCaseCharactersChecker());

    return checker;
  }

}
