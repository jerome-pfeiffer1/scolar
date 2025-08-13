statechart TestSC {
  final state F

  state B;

  initial state A;

  A -> B ['c'] / {PRINT};
  B -> F ['d'] / {EXEC};
}