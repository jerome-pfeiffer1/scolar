/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

package languagecomponentbase.cocos;

import java.util.Optional;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.*;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

/**
 * Checks whether the referenced grammar exists.
 *
 * @author Jerome Pfeiffer
 */

public class ReferencedGrammarExists implements LanguageComponentBaseASTLanguageComponentCoCo {
    @Override
    public void check(ASTLanguageComponent node) {
        IGrammar_WithConceptsGlobalScope globalScope = Grammar_WithConceptsMill.globalScope();
        String asReference = node.getASReference();

        Optional<MCGrammarSymbol> grammar = globalScope.resolveMCGrammar(asReference);

        if (!grammar.isPresent()) {
            Log.error(
                    String.format("MC001 Referenced grammar %s in language component %s does not exist!",
                            node.getASReference(),
                            node.getName()),
                    node.get_SourcePositionStart());
            return;
        }
    }
}