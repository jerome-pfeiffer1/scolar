package sc.finalstate._generator;

import sc._generator._producer.IAction2JavaGenerator;
import sc._generator._producer.IGuardExpr2JavaGenerator;
import sc._generator._producer.IState2JavaGen;
import sc.finalstate._ast.ASTFState;
import sc.statechart._ast.ASTIState;

import java.nio.file.Path;

public class IFinalStateGenerator2IState2JavaGen extends IFinalStateGenerator2IState2JavaGenTOP {

  public IFinalStateGenerator2IState2JavaGen(IFinalStateGenerator adaptee) {
    super(adaptee);
  }

  @Override
  public void generate(ASTIState node, Path path) {
    adaptee.generate((ASTFState) node, path);
  }

  @Override
  public String getStateClassName(ASTIState state) {
    return ((ASTFState) state).getName();
  }

  @Override
  public String getStateName(ASTIState state) {
    return ((ASTFState) state).getName();
  }
}
