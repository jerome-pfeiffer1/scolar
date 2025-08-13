/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily.cocos;

import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.*;
import org.junit.Test;

/**
 * Tests for the language family cocos
 *
 * @author (last commit) Mutert
 */
public class LanguageFamilyCoCosTest extends AbstractCoCoTest {

    //@Test
    //public void testValid() { // Temporary disabled. Fails due to the new Coco "binding between symbols of same grammar".
    //    checkValid("general.montiarcexample", "AutomatenArchitektur");
    //}


    @Test
    public void testAllDeclaredFeaturesUsed() {
        final String name = "cocos.languagefamily.invalid.DeclaredFeaturesUsed";
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);
        LanguageFamilyCoCoChecker languageFamilyCoCoChecker = new LanguageFamilyCoCoChecker();
        languageFamilyCoCoChecker.addCoCo(new AllDeclaredFeaturesUsed());
        checkInvalid(languageFamilyCoCoChecker, languageFamily,
                new ExpectedErrorInfo(1, "LF002"));
    }

    @Test
    public void testBindingTypesFit() {
        final String name = "cocos.languagefamily.invalid.BindingTypesFit";
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);

        checkInvalid(LanguageFamilyCoCos.createChecker(), languageFamily,
                new ExpectedErrorInfo(9, "LF004", "LF005",
                        "LF006", "LF007", "LF008", "LF009", "LF018"));
    }

    @Test
    public void testUsedVPExist() {
        final String name = "cocos.languagefamily.invalid.UsedVPExist";
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);

        checkInvalid(LanguageFamilyCoCos.createChecker(), languageFamily,
                new ExpectedErrorInfo(6, "LF010", "LF004", "LF009", "LF018"));
    }

    @Test
    public void testUsedFeaturesExist() {
        final String name = "cocos.languagefamily.invalid.UsedFeaturesExist";
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);

        final LanguageFamilyCoCoChecker checker = new LanguageFamilyCoCoChecker();
        checker.addCoCo(new UsedFeaturesExist());
        checkInvalid(checker, languageFamily,
                new ExpectedErrorInfo(3, "LF001"));
    }

    @Test
    public void testRootFeatureIsAbstract() {
        final String name = "cocos.languagefamily.invalid.RootFeatureIsNotAbstract";
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);

        final LanguageFamilyCoCoChecker checker = new LanguageFamilyCoCoChecker();
        checker.addCoCo(new RootFeatureIsNotAbstract());
        checkInvalid(checker, languageFamily,
                new ExpectedErrorInfo(1, "LF016"));
    }

    @Test
    public void testBindingBetweenSymbolsOfSameGrammar() {
        final String name = "cocos.languagefamily.invalid.BindingBetweenSymbolsOfSameGrammar";
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);

        final LanguageFamilyCoCoChecker checker = new LanguageFamilyCoCoChecker();
        checker.addCoCo(new BindingBetweenSymbolsOfSameGrammar());
        checkInvalid(checker, languageFamily,
                new ExpectedErrorInfo(3, "LF018"));
    }
}
