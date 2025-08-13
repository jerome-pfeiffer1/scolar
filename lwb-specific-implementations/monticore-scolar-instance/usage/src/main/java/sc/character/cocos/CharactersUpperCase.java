package sc.character.cocos;

import de.se_rwth.commons.logging.Log;
import sc.character._ast.ASTCharacterRule;
import sc.character._cocos.CharacterASTCharacterRuleCoCo;

public class CharactersUpperCase implements CharacterASTCharacterRuleCoCo {

  @Override
  public void check(ASTCharacterRule node) {
    if(!Character.isLowerCase(node.getCharacter().getValue())) {
      Log.error("Characters should be only given in upper case.");
    }
  }
}
