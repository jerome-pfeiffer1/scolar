package sc.charguard._generator;

public class ICharacter2IGuardExpr extends ICharacter2IGuardExprTOP {

  public ICharacter2IGuardExpr(ICharacter adaptee) {
    super(adaptee);
  }


  @Override
  public boolean eval(String input) {
    return adaptee.getCharacter().toString().equals(input);
  }
}
