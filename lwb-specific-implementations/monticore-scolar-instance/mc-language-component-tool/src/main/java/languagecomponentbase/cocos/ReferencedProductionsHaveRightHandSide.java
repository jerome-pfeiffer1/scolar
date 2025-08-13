package languagecomponentbase.cocos;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTProvidedExtension;
import languagecomponentbase._ast.ASTRequiredExtension;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks if the grammar production used by an extension point has a right hand side that can be referenced by a name.
 *
 */

public class ReferencedProductionsHaveRightHandSide implements LanguageComponentBaseASTLanguageComponentCoCo {

    @Override
    public void check(ASTLanguageComponent node) {
        IGrammar_WithConceptsGlobalScope globalScope = Grammar_WithConceptsMill.globalScope();
        String asReference = node.getASReference();

        Optional<MCGrammarSymbol> grammar = globalScope.resolveMCGrammar(asReference);

        if (!grammar.isPresent()) {
            return;
        }

        // Check if the referenced grammar rules for the extension points has a right hand side to reference
        for (final ASTRequiredExtension extensionPoint : node.getExtensionPoints()) {

            // Right hand side should have a name only when composition is an aggregation
            if (extensionPoint.isPresentComposition() && extensionPoint.isAggregate()) {
                String ruleName = extensionPoint.getName();
                if (extensionPoint.isPresentReferencedRule()) {
                    final String ruleNameOpt = extensionPoint.getReferencedRule();
                    if (!ruleNameOpt.isEmpty()) {
                        ruleName = ruleNameOpt;
                    }
                }

                final Optional<ProdSymbol> prodWithInherited = grammar.get().getProd(ruleName);

                if (prodWithInherited.isPresent()) {
                    // List for all tokens of production
                    List<String> tokens = new ArrayList<>();
                    for (RuleComponentSymbol a : prodWithInherited.get().getProdComponents()) {
                        tokens.add(a.getName());
                    }
                    // Test if list contains a name token which can be referenced
                    if (!tokens.contains("name")) {
                        Log.error(
                                String.format("MC005 Referenced grammar %s in language component %s has production that can not be referenced " +
                                                "due to missing Name-keyword on the right hand side.",
                                        node.getASReference(),
                                        node.getName()),
                                node.get_SourcePositionStart());
                    }
                }
            }
        }
/*
        // TODO needs some kind of checking if composition is embedding or aggregation so that only productions which get aggregated throw an error
        // Check that the referenced grammar rules for the provision points has a right hand side to reference
        // this could be solved by using the parameter ASTLanguageFamily for the check-method to get the composition type of a binding
        for (final ASTProvidedExtension provisionPoint : node.getProvisionPoints()) {
            String ruleName = provisionPoint.getName();
            if (provisionPoint.isPresentReferencedRule()) {
                final String ruleNameOpt = provisionPoint.getReferencedRule();
                if (!ruleNameOpt.isEmpty()) {
                    ruleName = ruleNameOpt;
                }
            }

            final Optional<ProdSymbol> prodWithInherited = grammar.get().getProd(ruleName);

            if (prodWithInherited.isPresent()) {
                List<String> tokens = new ArrayList<>();
                for (RuleComponentSymbol a : prodWithInherited.get().getProdComponents()) {
                    tokens.add(a.getName());
                }
                if (!tokens.contains("name")) {
                    Log.error(
                            String.format("MC006 Referenced grammar %s in language component %s has production that can not be referenced " +
                                            "due to missing Name-keyword on the right hand side.",
                                    node.getASReference(),
                                    node.getName()),
                            node.get_SourcePositionStart());
                }
            }
        }
        */
    }
}
