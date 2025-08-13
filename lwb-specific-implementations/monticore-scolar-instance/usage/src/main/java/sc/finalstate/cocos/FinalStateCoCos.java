package sc.finalstate.cocos;

import sc.finalstate._cocos.FinalStateCoCoChecker;

public class FinalStateCoCos {

  /**
   * Creates a CoCo-Checker for the "FinalStateCoCoChecker" CoCo-Set.
   * @return
   */
  public FinalStateCoCoChecker createFinalStateCoCosChecker() {
    FinalStateCoCoChecker checker = new FinalStateCoCoChecker();

    checker.addCoCo(new FinalStateNameUpperCase());

    return checker;
  }
}
