package _adapter;

    
import gencomposition.domainmodel.statechart.*;
import gencomposition.domainmodel.montiarc.*;

public abstract class ISCGen2IBehaviorGen implements IBehaviorGen {
  
  private ISCGen delegate;
  
  public void setDelegate(ISCGen delegate) {
    this.delegate = delegate;
  }
  
  public ISCGen getDelegate() {
    return this.delegate;
  }
  
}
