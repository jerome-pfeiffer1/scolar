package characterexpression.characterexpression.cocos;

import characterexpression.characterexpression._cocos.CharacterExpressionCoCoChecker;

public class CharExprCoCos {

  /**
   * Creates a CoCo-Checker for the "UpperCaseCharacters" CoCo-Set.
   * @return
   */
  public CharacterExpressionCoCoChecker createUpperCaseCharactersChecker() {
    CharacterExpressionCoCoChecker checker = new CharacterExpressionCoCoChecker();

    checker.addCoCo(new CharactersUpperCase());

    return checker;
  }
}
