package languagefamily._cocos;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily._ast.ASTBindingElement;
import languagefamily._ast.ASTFeatureDeclaration;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.util.Util;

import java.util.Optional;

/**
 * Checks if composition takes place between two symbols of the same language.
 *
 */
public class BindingBetweenSymbolsOfSameGrammar implements LanguageFamilyASTLanguageFamilyCoCo {

    @Override
    public void check(ASTLanguageFamily node) {

        for (ASTFeatureDeclaration featureDeclaration : node.getFeaturesList()) {
            if (featureDeclaration.isPresentRealizingComponentName()) {
                ASTMCQualifiedName ppLanguageComponentName = featureDeclaration.getRealizingComponentName();

                for (ASTBindingElement bindingElement : featureDeclaration.getBindingElementList()) {
                    if (bindingElement.isPresentEpFeature()) {
                        String ppName = bindingElement.getProvidedElement();
                        String epName = bindingElement.getEpName();
                        Optional<ASTMCQualifiedName> epLanguageComponentName =
                                Util.getRealizingLanguageComponentName(
                                        bindingElement.getEpFeature(),
                                        node.getFeaturesList());

                        Optional<LanguageComponentSymbol> ppLanguageComponent = node.getEnclosingScope()
                                .resolveLanguageComponent(ppLanguageComponentName.toString());
                        if (ppLanguageComponent.isEmpty()) {
                            return;
                        }

                        Optional<LanguageComponentSymbol> epLanguageComponent = Optional.empty();
                        if (epLanguageComponentName.isPresent()) {
                            epLanguageComponent = node.getEnclosingScope()
                                    .resolveLanguageComponent(epLanguageComponentName.get().toString());
                        }

                        String ppGrammarName = ppLanguageComponent.get().getAstNode().getASReference();
                        String epGrammarName = epLanguageComponent.get().getAstNode().getASReference();

                        if (ppGrammarName.equals(epGrammarName)) {
                            Log.error("LF018 Binding between " + ppName + " and " + epName + " of the same grammar "
                                    + ppGrammarName + ". ", featureDeclaration.get_SourcePositionStart());
                        }
                    }
                }
            }
        }
    }
}

