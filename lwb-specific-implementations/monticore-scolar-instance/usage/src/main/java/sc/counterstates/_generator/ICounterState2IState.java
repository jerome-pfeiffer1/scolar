package sc.counterstates._generator;

public class ICounterState2IState extends ICounterState2IStateTOP{

  public ICounterState2IState(ICounterState adaptee) {
    super(adaptee);
  }

  @Override
  public String getName() {
    return adaptee.getName();
  }

  @Override
  public void visit() {
    adaptee.visit();
  }
}
