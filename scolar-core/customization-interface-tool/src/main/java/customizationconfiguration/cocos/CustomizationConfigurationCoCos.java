/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationconfiguration.cocos;

import customizationconfiguration._cocos.CustomizationConfigurationCoCoChecker;

/**
 * CoCo checker for customization configuration artifacts
 *
 * @author Michael Mutert
 */
public class CustomizationConfigurationCoCos {

  public static CustomizationConfigurationCoCoChecker createChecker() {
    CustomizationConfigurationCoCoChecker checker = new CustomizationConfigurationCoCoChecker();

    checker.addCoCo(new ImportedLanguageComponentsExist());
    checker.addCoCo(new ReferencedLanguageComponentsAreImported());
    checker.addCoCo(new ReferencedElementsExist());
    checker.addCoCo(new ReferencedStartPPsExist());

    return checker;
  }
}
