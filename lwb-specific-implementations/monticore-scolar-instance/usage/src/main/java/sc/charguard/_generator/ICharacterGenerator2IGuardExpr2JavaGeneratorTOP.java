package sc.charguard._generator;

    
import sc.charguard._generator.*;
import sc._generator._producer.*;

public abstract class ICharacterGenerator2IGuardExpr2JavaGeneratorTOP implements IGuardExpr2JavaGenerator {
  
  protected ICharacterGenerator adaptee;
  
  public ICharacterGenerator2IGuardExpr2JavaGeneratorTOP(ICharacterGenerator adaptee) {
    this.adaptee = adaptee;  
  }
  
  @Override
  public Class<?> getTargetInterface() {
    return ICharacter2IGuardExpr.class;  
  }
}
