package sc.counterstates.cocos;

import de.se_rwth.commons.logging.Log;
import sc.counterstates._ast.ASTCounterState;
import sc.counterstates._cocos.CounterStatesASTCounterStateCoCo;

public class StepValuePositive implements CounterStatesASTCounterStateCoCo {

  @Override
  public void check(ASTCounterState node) {
    if (node.getStepValue().getValue() <= 0) {
      Log.error(String.format("Step value is %d and thus not positive",
          node.getStepValue().getValue()),
          node.getStepValue().get_SourcePositionStart());
    }
  }
}
