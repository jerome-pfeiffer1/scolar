package character._generator;

import character._generator.*;
import aut._generator.guard._product.IGuardExpr;
import characterexpression._generator.ICharacter;


public abstract class ICharacter2IGuardExprTOP implements IGuardExpr {
  
  protected ICharacter adaptee;
  
  public ICharacter2IGuardExprTOP(ICharacter adaptee) {
    this.adaptee = adaptee;  
  }
  
}
