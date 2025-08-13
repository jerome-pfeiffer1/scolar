package sc.finalstate._generator;

    
import sc.finalstate._generator.*;
import sc._generator._product.*;

public abstract class IFinalState2IStateTOP implements IState {
  
  protected IFinalState adaptee;
  
  public IFinalState2IStateTOP(IFinalState adaptee) {
    this.adaptee = adaptee;  
  }
  
}
