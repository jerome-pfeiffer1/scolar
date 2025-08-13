package sc.character.cocos;

import sc.character._cocos.CharacterCoCoChecker;

public class CharGuardCoCos {

  /**
   * Creates a CoCo-Checker for the "UpperCaseCharacters" CoCo-Set.
   * @return
   */
  public CharacterCoCoChecker createUpperCaseCharactersChecker() {
    CharacterCoCoChecker checker = new CharacterCoCoChecker();

    checker.addCoCo(new CharactersUpperCase());

    return checker;
  }
}
