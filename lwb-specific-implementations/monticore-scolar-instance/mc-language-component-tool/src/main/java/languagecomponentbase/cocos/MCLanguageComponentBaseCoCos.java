/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase.cocos;

import languagecomponentbase._cocos.LanguageComponentBaseCoCoChecker;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class MCLanguageComponentBaseCoCos {

    public static LanguageComponentBaseCoCoChecker createChecker() {
        final LanguageComponentBaseCoCoChecker languageComponentCoCoChecker = LanguageComponentBaseCoCos.createChecker();

        languageComponentCoCoChecker.addCoCo(new ReferencedGrammarExists());
        languageComponentCoCoChecker.addCoCo(new ReferencedRuleInEPExists());
        languageComponentCoCoChecker.addCoCo(new ReferencedRuleInEPIsAnInterface());
        languageComponentCoCoChecker.addCoCo(new ReferencedRuleInPPExists());

        languageComponentCoCoChecker.addCoCo(new ReferencedProductionsHaveRightHandSide());

        return languageComponentCoCoChecker;
    }
}
