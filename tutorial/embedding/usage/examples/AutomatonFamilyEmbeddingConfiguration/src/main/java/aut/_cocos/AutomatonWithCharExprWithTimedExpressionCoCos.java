package aut._cocos;

import java.util.Optional;

import aut.automatongrammarwithcharacterexpressionwithclockexpression._cocos.AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker;

import characterexpression.characterexpression.cocos.CharExprCoCos;
import timedexpr.clockexpression.cocos.TimedExpressionCoCos;
import aut.automaton.cocos.AutomatonCoCos;


public class AutomatonWithCharExprWithTimedExpressionCoCos  {

  public AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker createTransitionsCorrectChecker() {

    AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker checker = new AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker();

    AutomatonCoCos automaton = new AutomatonCoCos();
    checker.addChecker(automaton.createTransitionsCorrectChecker());

    return checker;
  }

  public AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker createUpperCaseCharactersChecker() {

    AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker checker = new AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker();

    CharExprCoCos charexpr = new CharExprCoCos();
    checker.addChecker(charexpr.createUpperCaseCharactersChecker());

    return checker;
  }

  public AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker createTimeCorrectnessChecksChecker() {

    AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker checker = new AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker();

    TimedExpressionCoCos timedexpression = new TimedExpressionCoCos();
    checker.addChecker(timedexpression.createTimeCorrectnessChecker());

    return checker;
  }

}
