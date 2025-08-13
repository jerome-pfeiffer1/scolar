package character._generator;

    
import aut._generator.guard._producer.IGuardExpr2JavaGenerator;
import character._generator.ICharacter2IGuardExpr;
import aut.automatongrammar._ast.ASTIGuardExpr;
import characterexpression._generator.ICharacterGenerator;

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
