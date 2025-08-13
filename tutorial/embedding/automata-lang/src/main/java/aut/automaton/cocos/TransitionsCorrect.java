package aut.automaton.cocos;

import aut.automatongrammar._ast.ASTTransition;
import aut.automatongrammar._cocos.AutomatonGrammarASTTransitionCoCo;
import de.se_rwth.commons.logging.Log;

public class TransitionsCorrect implements AutomatonGrammarASTTransitionCoCo {

  @Override
  public void check(ASTTransition node) {
    if(node.getSource().equals(node.getTarget())) {
      Log.error("No self loops allowed", node.get_SourcePositionStart());
    }
  }
}
