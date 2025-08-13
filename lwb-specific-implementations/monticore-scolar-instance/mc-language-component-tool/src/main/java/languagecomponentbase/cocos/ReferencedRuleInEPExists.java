package languagecomponentbase.cocos;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTRequiredExtension;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

import java.util.Optional;

public class ReferencedRuleInEPExists implements LanguageComponentBaseASTLanguageComponentCoCo {
    @Override
    public void check(ASTLanguageComponent node) {
        IGrammar_WithConceptsGlobalScope globalScope = Grammar_WithConceptsMill.globalScope();
        String asReference = node.getASReference();

        Optional<MCGrammarSymbol> grammar = globalScope.resolveMCGrammar(asReference);

        if (!grammar.isPresent()) {
            return;
        }

        // Check that the referenced grammar rules for the extension points exist
        for (final ASTRequiredExtension extensionPoint : node.getExtensionPoints()) {
            String ruleName = extensionPoint.getName();
            if (extensionPoint.isPresentReferencedRule()) {
                final String ruleNameOpt = extensionPoint.getReferencedRule();
                if (!ruleNameOpt.isEmpty()) {
                    ruleName = ruleNameOpt;
                }
            }

            final Optional<ProdSymbol> prodWithInherited = grammar.get().getProd(ruleName);
            //this.getSpannedScope().resolveProdLocally(prodName);

            if (!prodWithInherited.isPresent()) {
                Log.error(
                        String.format("MC002 The referenced rule %s of the extension point %s " +
                                        "in language component %s is not a rule of the referenced grammar %s.",
                                ruleName,
                                extensionPoint.getName(),
                                node.getName(),
                                node.getASReference()),
                        extensionPoint.get_SourcePositionStart());
            }
        }
    }
}
