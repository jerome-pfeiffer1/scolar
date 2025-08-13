package _adapter;

    
import gencomposition.domainmodel.statechart.*;
import gencomposition.domainmodel.montiarc.*;

public abstract class ISCRTEBehavior2IComputable implements IComputable {
  
  private ISCRTEBehavior delegate;
  
  public void setDelegate(ISCRTEBehavior delegate) {
    this.delegate = delegate;
  }
  
  public ISCRTEBehavior getDelegate() {
    return this.delegate;
  }
  
}
