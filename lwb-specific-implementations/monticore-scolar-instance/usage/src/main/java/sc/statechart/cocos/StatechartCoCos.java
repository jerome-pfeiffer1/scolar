package sc.statechart.cocos;

import sc.statechart._cocos.StatechartCoCoChecker;

import java.util.Optional;

public class StatechartCoCos {

  private Optional<Integer> maxNumberOfStates = Optional.of(1);
  public void setMaxNumberOfStates(Optional<Integer> maxNumberOfStates) {
    this.maxNumberOfStates = maxNumberOfStates;
  }

  /**
   * Creates a CoCo-Checker for the "InitialStateCoCos" CoCo-Set.
   * @return
   */
  public StatechartCoCoChecker createInitialStateCoCosChecker() {
    StatechartCoCoChecker checker = new StatechartCoCoChecker();


    final NumberOfInitialStatesCorrect numberOfInitialStatesCorrect = new NumberOfInitialStatesCorrect();
    maxNumberOfStates.ifPresent(numberOfInitialStatesCorrect::setMaxNumberOfStates);
    checker.addCoCo(numberOfInitialStatesCorrect);

    return checker;
  }

  /**
   * Creates a CoCo-Checker for the "TransitionsCorrect" CoCo-Set.
   * @return
   */
  public StatechartCoCoChecker createTransitionsCorrectChecker() {
    StatechartCoCoChecker checker = new StatechartCoCoChecker();

    checker.addCoCo(new TransitionsCorrect());

    return checker;
  }
}
