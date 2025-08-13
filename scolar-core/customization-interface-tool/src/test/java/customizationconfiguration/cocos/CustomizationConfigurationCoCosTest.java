/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationconfiguration.cocos;

import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._cocos.CustomizationConfigurationCoCoChecker;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Tests the context conditions for the Customization Configuration language
 *
 * @author (last commit) Michael Mutert
 */
public class CustomizationConfigurationCoCosTest extends AbstractCoCoTest {


  @Ignore
  @Test
  public void testImportedLanguageComponentsExist() {
    String name = "cocos.customizationconfiguration.ImportedLanguageComponentsDoNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ImportedLanguageComponentsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC001"));
  }

  @Ignore
  @Test
  public void testReferencedLanguageComponentsNotImported() {
    String name = "cocos.customizationconfiguration.ReferencedLanguageComponentsNotImported";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedLanguageComponentsAreImported());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC002"));
  }

  @Ignore
  @Test
  public void testReferencedLCDoesNotExist() {
    String name = "cocos.customizationconfiguration.ReferencedLCDoesNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedElementsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC003"));
  }

  @Ignore
  @Test
  public void testBoundParameterDoesNotExist() {
    String name = "cocos.customizationconfiguration.BoundParameterDoesNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedElementsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC004"));
  }

  @Ignore
  @Test
  public void testBoundCPDoesNotExist() {
    String name = "cocos.customizationconfiguration.BoundCPDoesNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedElementsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC005"));
  }

  @Ignore
  @Test
  public void testReferencedPPDoesNotExist() {
    String name = "cocos.customizationconfiguration.ReferencedPPDoesNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedElementsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC006"));
  }

  @Ignore
  @Test
  public void testReferencedPPIsOfWrongType() {
    String name = "cocos.customizationconfiguration.ReferencedPPIsOfWrongType";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedElementsExist());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "CC009");
    checkInvalid(coCoChecker, configuration, errors);
  }

  @Ignore
  @Test
  public void testNoCPOnASBinding() {
    String name = "cocos.customizationconfiguration.NoCPOnASBinding";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    final CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedElementsExist());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "CC013");
    checkInvalid(coCoChecker, configuration, errors);
  }

}
