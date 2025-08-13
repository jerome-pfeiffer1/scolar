package sc.counterstates._generator;

public interface ICounterState {

  String getName();

  Integer getCounterValue();

  Integer getStepValue();

  void visit();
}
