package languagecomponentbase.cocos;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily._ast.ASTBindingElement;
import languagefamily._ast.ASTFeatureDeclaration;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.LanguageFamilyASTBindingElementCoCo;
import languagefamily._cocos.LanguageFamilyASTLanguageFamilyCoCo;
import languagefamily._cocos.util.Util;

import java.util.Optional;

/**
 * Checks if the grammar production used by an extension point has a right hand side that can be referenced by a name.
 * Has currently no use.
 *
 */
public class ReferencedProductionsHaveRightHandSideBeta implements LanguageFamilyASTLanguageFamilyCoCo {
    @Override
    public void check(ASTLanguageFamily node) {

        System.out.println("Hier sind wir");
        /*
        for (ASTFeatureDeclaration featureDeclaration : node.getFeaturesList()) {

            if (featureDeclaration.isPresentRealizingComponentName()) {

                ASTMCQualifiedName ppLanguageComponentName = featureDeclaration.getRealizingComponentName();

                featureDeclaration.getBindingElementList();

                for (ASTBindingElement bindingElement : featureDeclaration.getBindingElementList()) {
                    if (bindingElement.isPresentEpFeature()) {

                        String ppName = bindingElement.getProvidedElement();
                        String epName = bindingElement.getEpName();
                        Optional<ASTMCQualifiedName> epLanguageComponentName =
                                Util.getRealizingLanguageComponentName(
                                        bindingElement.getEpFeature(),
                                        node.getFeaturesList());
                    }
                }
            }
        }*/
    }
}
