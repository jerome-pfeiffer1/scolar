package sc;

customization StatechartsWFACConfig for sc.StateChartWithFinalStateWithCharGuard {

  root configuration {
    production SC;
    gen SC2Java;
    wfrs InitialStateCoCos;
    wfrs UpperCaseCharacters;
  }

  bind production sc.counterstates.CounterState.CounterState -> State;
  bind gen sc.counterstates.CounterState.CounterState2Java -> State2Java;
  bind wfrs sc.counterstates.CounterState.StepPositive -> State;

  assign maxNumberOfStates = 5;
  assign actionInterpreter = sc._generator.StatementPrinter();

}

