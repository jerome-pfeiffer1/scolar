/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase.cocos;

import languagecomponentbase._cocos.LanguageComponentBaseCoCoChecker;

/**
 * Default coco checker for Language Components
 *
 * @author  Pfeiffer
 * @author Michael Mutert
 *
 */
public class LanguageComponentBaseCoCos {
 
  public static LanguageComponentBaseCoCoChecker createChecker() {
    final LanguageComponentBaseCoCoChecker languageComponentCoCoChecker =
        new LanguageComponentBaseCoCoChecker();

    languageComponentCoCoChecker.addCoCo(new AtLeastOnePPOrWfrSet());

    languageComponentCoCoChecker.addCoCo(new PPAndEPStartUpperCase());
    languageComponentCoCoChecker.addCoCo(new PPAndEPNamesUnique());

    languageComponentCoCoChecker.addCoCo(new ReferencedDomainModelReferencesExist());

    languageComponentCoCoChecker.addCoCo(new ImplicationSourcesAndTargetsOfCorrectType());
    languageComponentCoCoChecker.addCoCo(new ImplicationsTargetsAreEPs());

    languageComponentCoCoChecker.addCoCo(new LCContainsGrammarMultipleTimes());

    return languageComponentCoCoChecker;
  }

}
