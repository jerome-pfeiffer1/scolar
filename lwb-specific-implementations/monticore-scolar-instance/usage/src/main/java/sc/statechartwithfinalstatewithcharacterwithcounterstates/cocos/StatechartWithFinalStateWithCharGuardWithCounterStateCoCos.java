package sc.statechartwithfinalstatewithcharacterwithcounterstates.cocos;

import java.util.Optional;

import sc.statechartwithfinalstatewithcharacterwithcounterstates._cocos.StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker;

import sc.counterstates.cocos.CounterStateCoCos;
import sc.statechartwithfinalstatewithcharacter.cocos.StatechartWithFinalStateWithCharGuardCoCos;


public class StatechartWithFinalStateWithCharGuardWithCounterStateCoCos  {

  public StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker createUpperCaseCharactersChecker() {

    StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker checker = new StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker();

    StatechartWithFinalStateWithCharGuardCoCos statechartWithFinalStateWithCharGuard = new StatechartWithFinalStateWithCharGuardCoCos();
    checker.addChecker(statechartWithFinalStateWithCharGuard.createUpperCaseCharactersChecker());

    return checker;
  }

  public StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker createInitialStateCoCosChecker() {

    StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker checker = new StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker();

    StatechartWithFinalStateWithCharGuardCoCos statechartWithFinalStateWithCharGuard = new StatechartWithFinalStateWithCharGuardCoCos();
    statechartWithFinalStateWithCharGuard.setMaxNumberOfStates(Optional.of(5));
    checker.addChecker(statechartWithFinalStateWithCharGuard.createInitialStateCoCosChecker());

    return checker;
  }

  public StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker createStepPositiveChecker() {

    StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker checker = new StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker();

    CounterStateCoCos counterState = new CounterStateCoCos();
    checker.addChecker(counterState.createStepPositiveChecker());

    return checker;
  }

}
