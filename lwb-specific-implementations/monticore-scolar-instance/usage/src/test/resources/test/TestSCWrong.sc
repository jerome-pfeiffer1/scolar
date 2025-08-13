statechart TestSCWrong {
  final state f

  state B;

  initial state A;

  A -> B ['c'] / {PRINT};
  B -> C ['d'] / {EXEC};
}