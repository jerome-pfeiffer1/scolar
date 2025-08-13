statechart TestSCWithCounter {
  final state F

  state B;

  initial state A;

  counter state CState, step 2, start 0;

  A -> B ['b'] / {PRINT};
  B -> CState ['c'] / {EXEC};
  CState -> CState ['c'] / {EXEC};
  CState -> F ['e'] / {EXEC};
}