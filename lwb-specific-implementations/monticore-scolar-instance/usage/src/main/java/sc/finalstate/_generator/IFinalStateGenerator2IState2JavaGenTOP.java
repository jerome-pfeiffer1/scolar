package sc.finalstate._generator;

    
import sc.finalstate._generator.*;
import sc._generator._producer.*;

public abstract class IFinalStateGenerator2IState2JavaGenTOP implements IState2JavaGen {
  
  protected IFinalStateGenerator adaptee;
  
  public IFinalStateGenerator2IState2JavaGenTOP(IFinalStateGenerator adaptee) {
    this.adaptee = adaptee;  
  }
  
  @Override
  public Class<?> getTargetInterface() {
    return IFinalState2IState.class;  
  }
}
