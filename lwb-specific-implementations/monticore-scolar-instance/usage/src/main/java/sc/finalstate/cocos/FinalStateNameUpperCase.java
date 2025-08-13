package sc.finalstate.cocos;

import de.se_rwth.commons.logging.Log;
import sc.finalstate._ast.ASTFState;
import sc.finalstate._cocos.FinalStateASTFStateCoCo;

public class FinalStateNameUpperCase implements FinalStateASTFStateCoCo {

  @Override
  public void check(ASTFState node) {
    if(Character.isLowerCase(node.getName().charAt(0))){
      Log.error("The final state has to start with an upper case letter.");
    }
  }
}
