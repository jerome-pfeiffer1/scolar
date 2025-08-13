package sc.statechartwithfinalstatewithcharacter.cocos;

import java.util.Optional;

import sc.statechartwithfinalstatewithcharacter._cocos.StatechartWithFinalStateWithCharacterCoCoChecker;

import sc.character.cocos.CharGuardCoCos;
import sc.statechart.cocos.StatechartCoCos;
import sc.finalstate.cocos.FinalStateCoCos;


public class StatechartWithFinalStateWithCharGuardCoCos  {

  private Optional<Integer> maxNumberOfStates = Optional.empty();
  public void setMaxNumberOfStates(Optional<Integer> maxNumberOfStates) {
    this.maxNumberOfStates = maxNumberOfStates;
  }

  public StatechartWithFinalStateWithCharacterCoCoChecker createInitialStateCoCosChecker() {

    StatechartWithFinalStateWithCharacterCoCoChecker checker = new StatechartWithFinalStateWithCharacterCoCoChecker();

    StatechartCoCos statechart = new StatechartCoCos();
    statechart.setMaxNumberOfStates(maxNumberOfStates);
    checker.addChecker(statechart.createInitialStateCoCosChecker());

    FinalStateCoCos finalState = new FinalStateCoCos();
    checker.addChecker(finalState.createFinalStateCoCosChecker());

    return checker;
  }

  public StatechartWithFinalStateWithCharacterCoCoChecker createUpperCaseCharactersChecker() {

    StatechartWithFinalStateWithCharacterCoCoChecker checker = new StatechartWithFinalStateWithCharacterCoCoChecker();

    CharGuardCoCos charGuard = new CharGuardCoCos();
    checker.addChecker(charGuard.createUpperCaseCharactersChecker());

    return checker;
  }

}
