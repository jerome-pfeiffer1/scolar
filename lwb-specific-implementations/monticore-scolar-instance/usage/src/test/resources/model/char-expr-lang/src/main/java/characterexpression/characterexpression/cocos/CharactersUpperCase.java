package characterexpression.characterexpression.cocos;

import characterexpression.characterexpression._ast.ASTCharacterRule;
import de.se_rwth.commons.logging.Log;

public class CharactersUpperCase implements characterexpression.characterexpression._cocos.CharacterExpressionASTCharacterRuleCoCo {


  /**
   * @see characterexpression.characterexpression._cocos.CharacterExpressionASTCharacterRuleCoCo#check(characterexpression.characterexpression._ast.ASTCharacterRule)
   */
  @Override
  public void check(ASTCharacterRule node) {
    if(!Character.isLowerCase(node.getCharacter().getValue())) {
      Log.error("Characters should be only given in upper case.");
    }
  }
}
