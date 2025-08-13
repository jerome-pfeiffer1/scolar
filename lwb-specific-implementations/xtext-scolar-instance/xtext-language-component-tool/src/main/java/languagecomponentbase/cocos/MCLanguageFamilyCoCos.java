package languagecomponentbase.cocos;

import languagefamily.LanguageFamilyMill;
import languagefamily._cocos.LanguageFamilyCoCoChecker;
import languagefamily._cocos.LanguageFamilyCoCos;

/**
 * Coco Checker for MCLanguageFamilyCoCos. Has currently no use.
 */
public class MCLanguageFamilyCoCos {

    public static LanguageFamilyCoCoChecker createChecker() {
        final LanguageFamilyCoCoChecker test = LanguageFamilyCoCos.createChecker();

        //final LanguageFamilyCoCoChecker languageFamilyCoCoChecker = new LanguageFamilyCoCoChecker();
        //languageFamilyCoCoChecker.setTraverser(LanguageFamilyMill.traverser());

        //test.addCoCo(new ReferencedProductionsHaveRightHandSideBeta());

        return test;
    }
}
