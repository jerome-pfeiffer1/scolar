package languagecomponentbase.cocos;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTProvidedExtension;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

import java.util.Optional;

public class ReferencedRuleInPPExists implements LanguageComponentBaseASTLanguageComponentCoCo {

    @Override
    public void check(ASTLanguageComponent node) {
        IGrammar_WithConceptsGlobalScope globalScope = Grammar_WithConceptsMill.globalScope();
        String asReference = node.getASReference();

        Optional<MCGrammarSymbol> grammar = globalScope.resolveMCGrammar(asReference);

        if (!grammar.isPresent()) {
            return;
        }

        // Check that the referenced grammar rules for the provision points exist
        for (final ASTProvidedExtension provisionPoint : node.getProvisionPoints()) {
            String ruleName = provisionPoint.getName();

            if (provisionPoint.isPresentReferencedRule()) {
                final String ruleNameOpt = provisionPoint.getReferencedRule();
                if (!ruleNameOpt.isEmpty()) {
                    ruleName = ruleNameOpt;
                }
            }
            final Optional<ProdSymbol> prodWithInherited = grammar.get().getProd(ruleName);

            if (!prodWithInherited.isPresent()) {
                Log.error(
                        String.format("MC003 The referenced rule %s of the provision point %s " +
                                        "in language component %s is not a rule of the referenced grammar %s.",
                                ruleName,
                                provisionPoint.getName(),
                                node.getName(),
                                node.getASReference()),
                        provisionPoint.get_SourcePositionStart());
            }
        }
    }
}
