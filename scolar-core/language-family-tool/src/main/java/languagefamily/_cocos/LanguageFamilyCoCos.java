package languagefamily._cocos;

import languagefamily.LanguageFamilyMill;

public class LanguageFamilyCoCos {
  
  public static LanguageFamilyCoCoChecker createChecker() {
    final LanguageFamilyCoCoChecker checker = new LanguageFamilyCoCoChecker();
    checker.setTraverser(LanguageFamilyMill.traverser());
    
    checker.addCoCo(new ImportedLanguageComponentsExist());
    checker.addCoCo(new UsedFeaturesExist());
    checker.addCoCo(new AllDeclaredFeaturesUsed());
    checker.addCoCo(new BindingTypesFit());
    checker.addCoCo(new UsedVPExist());

    checker.addCoCo(new BindingBetweenSymbolsOfSameGrammar());

    return checker;
  }
}
