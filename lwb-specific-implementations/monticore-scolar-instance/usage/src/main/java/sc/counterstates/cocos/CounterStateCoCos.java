package sc.counterstates.cocos;

import sc.counterstates._cocos.CounterStatesCoCoChecker;

public class CounterStateCoCos {

  /**
   * Creates a CoCo-Checker for the "StepPositive" CoCo-Set.
   * @return
   */
  public CounterStatesCoCoChecker createStepPositiveChecker() {
    CounterStatesCoCoChecker checker = new CounterStatesCoCoChecker();

    checker.addCoCo(new StepValuePositive());

    return checker;
  }
}
