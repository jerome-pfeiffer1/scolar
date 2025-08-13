statechart TestSCWithCounterWrong {
  final state F

  state B;

  initial state A;

  counter state CState, step -1, start 0;

  A -> B ['c'] / {PRINT};
  B -> F ['d'] / {EXEC};
  F -> CState ['e'] / {EXEC};
}