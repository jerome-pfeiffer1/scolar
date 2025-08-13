package sc.finalstate._generator;

public class IFinalState2IState extends IFinalState2IStateTOP{

  public IFinalState2IState(IFinalState adaptee) {
    super(adaptee);
  }

  @Override
  public String getName() {
    return adaptee.getName();
  }

  @Override
  public void visit() {
    adaptee.terminate();
  }
}
