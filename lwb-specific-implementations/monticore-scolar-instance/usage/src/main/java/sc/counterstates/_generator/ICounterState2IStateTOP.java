package sc.counterstates._generator;

    
import sc.counterstates._generator.*;
import sc._generator._product.*;

public abstract class ICounterState2IStateTOP implements IState {
  
  protected ICounterState adaptee;
  
  public ICounterState2IStateTOP(ICounterState adaptee) {
    this.adaptee = adaptee;  
  }
  
}
