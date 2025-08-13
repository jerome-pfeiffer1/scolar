package aut.automaton.cocos;


import java.util.Optional;

import aut.automatongrammar._cocos.AutomatonGrammarCoCoChecker;

public class AutomatonCoCos {

  private Optional<Integer> maxNumberOfStates = Optional.of(1);
  public void setMaxNumberOfStates(Optional<Integer> maxNumberOfStates) {
    this.maxNumberOfStates = maxNumberOfStates;
  }

  /**
   * Creates a CoCo-Checker for the "InitialStateCoCos" CoCo-Set.
   * @return
   */
  public AutomatonGrammarCoCoChecker createInitialStateCoCosChecker() {
    AutomatonGrammarCoCoChecker checker = new AutomatonGrammarCoCoChecker();


    final NumberOfInitialStatesCorrect numberOfInitialStatesCorrect = new NumberOfInitialStatesCorrect();
    maxNumberOfStates.ifPresent(numberOfInitialStatesCorrect::setMaxNumberOfStates);
    checker.addCoCo(numberOfInitialStatesCorrect);

    return checker;
  }

  /**
   * Creates a CoCo-Checker for the "TransitionsCorrect" CoCo-Set.
   * @return
   */
  public AutomatonGrammarCoCoChecker createTransitionsCorrectChecker() {
    AutomatonGrammarCoCoChecker checker = new AutomatonGrammarCoCoChecker();

    checker.addCoCo(new TransitionsCorrect());

    return checker;
  }
}
