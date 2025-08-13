package sc.charguard._generator;

import sc._generator._producer.IAction2JavaGenerator;
import sc._generator._producer.IGuardExpr2JavaGenerator;
import sc._generator._producer.IState2JavaGen;
import sc.character._ast.ASTCharacterRule;
import sc.statechart._ast.ASTIGuardExpr;

import java.nio.file.Path;

public class ICharacterGenerator2IGuardExpr2JavaGenerator extends ICharacterGenerator2IGuardExpr2JavaGeneratorTOP {

  public ICharacterGenerator2IGuardExpr2JavaGenerator(ICharacterGenerator adaptee) {
    super(adaptee);
  }

  @Override
  public void generate(ASTIGuardExpr expr, Path path) {
    if(expr instanceof  ASTCharacterRule) {
      adaptee.generate((ASTCharacterRule) expr, path);
    }
  }

  @Override
  public String getGuardExprClassName(ASTIGuardExpr node) {
    return ((ASTCharacterRule) node).getCharacter().getSource();
  }
}
