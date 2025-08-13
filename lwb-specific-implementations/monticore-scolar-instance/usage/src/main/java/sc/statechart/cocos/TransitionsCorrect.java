package sc.statechart.cocos;

import de.se_rwth.commons.logging.Log;
import sc.statechart._ast.ASTTransition;
import sc.statechart._cocos.StatechartASTTransitionCoCo;

public class TransitionsCorrect implements StatechartASTTransitionCoCo {

  @Override
  public void check(ASTTransition node) {
    if(node.getSource().equals(node.getTarget())) {
      Log.error("No self loops allowed", node.get_SourcePositionStart());
    }
  }
}
