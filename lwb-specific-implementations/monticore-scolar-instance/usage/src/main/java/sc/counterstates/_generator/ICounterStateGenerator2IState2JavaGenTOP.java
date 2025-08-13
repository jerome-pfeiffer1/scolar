package sc.counterstates._generator;

    
import sc.counterstates._generator.*;
import sc._generator._producer.*;

public abstract class ICounterStateGenerator2IState2JavaGenTOP implements IState2JavaGen {
  
  protected ICounterStateGenerator adaptee;
  
  public ICounterStateGenerator2IState2JavaGenTOP(ICounterStateGenerator adaptee) {
    this.adaptee = adaptee;  
  }
  
  @Override
  public Class<?> getTargetInterface() {
    return ICounterState2IState.class;  
  }
}
