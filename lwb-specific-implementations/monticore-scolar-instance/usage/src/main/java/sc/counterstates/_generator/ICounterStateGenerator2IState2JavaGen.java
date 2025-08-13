package sc.counterstates._generator;

import sc._generator._producer.IAction2JavaGenerator;
import sc._generator._producer.IGuardExpr2JavaGenerator;
import sc._generator._producer.IState2JavaGen;
import sc.counterstates._ast.ASTCounterState;
import sc.statechart._ast.ASTIState;

import java.nio.file.Path;

public class ICounterStateGenerator2IState2JavaGen extends ICounterStateGenerator2IState2JavaGenTOP {

  public ICounterStateGenerator2IState2JavaGen(ICounterStateGenerator adaptee) {
    super(adaptee);
  }

  @Override
  public void generate(ASTIState node, Path path) {
    adaptee.generate((ASTCounterState) node, path);
  }

  @Override
  public String getStateClassName(ASTIState state) {
    return ((ASTCounterState) state).getName() + "CounterState";
  }

  @Override
  public String getStateName(ASTIState state) {
    return ((ASTCounterState) state).getName();
  }
}
