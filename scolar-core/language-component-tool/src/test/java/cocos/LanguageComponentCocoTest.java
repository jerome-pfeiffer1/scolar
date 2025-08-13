/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cocos;

import languagecomponentbase.cocos.LanguageComponentBaseCoCos;
import org.junit.Test;

/**
 * Tests for the context conditions of the Language Component language.
 *
 * @author Pfeiffer
 * @author Mutert
 */
public class LanguageComponentCocoTest extends AbstractCoCoTest{
  
  @Test
  public void testValid() {
    checkValid("general.montiarcexample.statecharts", "SC");
    checkValid("general.montiarcexample.montiarc", "MontiArc");
  }

  @Test
  public void testPPAndEPNamesStartUpperCase() {

    String name = INVALID_MODEL_PACKAGE + ".ASPPAndEPStartLowerCase";
    ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "LC003");
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name, errors);

    name = INVALID_MODEL_PACKAGE + ".GENPPAndEPStartLowerCase";
    errors = new ExpectedErrorInfo(2, "LC003", "LC004");
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name, errors);
  }

  @Test
  public void testPPAndEPNamesUnique() {

    String name = INVALID_MODEL_PACKAGE + ".ASPPAndEPNamesNotUnique";
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "LC002");
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name, errors);

    name = INVALID_MODEL_PACKAGE + ".GENPPAndEPNamesNotUnique";
    errors = new ExpectedErrorInfo(2, "LC001", "LC002");
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name, errors);

  }

  @Test
  public void testWFRSetNamesNotUnique() {
    String name = INVALID_MODEL_PACKAGE + ".WFRSetNamesNotUnique";
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "LC010");
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name, errors);

  }

  @Test
  public void testWFRSetNamesLowerCase() {
    String name = INVALID_MODEL_PACKAGE + ".WFRSetNamesStartLowerCase";

    ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "LC009");
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name, errors);
  }

  @Test
  public void testReferenceddomainModelExist() {
    final String name = INVALID_MODEL_PACKAGE + ".ReferencedProducerAndProductInterfacesExists";
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name,
        new ExpectedErrorInfo(3, "LC011"));
  }

  @Test
  public void testInvalidImplications() {
    final String name = INVALID_MODEL_PACKAGE + ".InvalidImplications";
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name,
        new ExpectedErrorInfo(7, "LC006", "LC007"));
  }

  @Test
  public void testNoPpEPOrWfrSet() {
    final String name = INVALID_MODEL_PACKAGE + ".NoPpEPOrWfrSet";
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name,
        new ExpectedErrorInfo(1, "LC013"));
  }

  @Test
  public void testLCContainsGrammarMultipleTimes() {
    final String name = INVALID_MODEL_PACKAGE + ".LCContainsGrammarMultipleTimes";
    checkInvalid(LanguageComponentBaseCoCos.createChecker(), name,
            new ExpectedErrorInfo(3, "LC022"));
  }
}
