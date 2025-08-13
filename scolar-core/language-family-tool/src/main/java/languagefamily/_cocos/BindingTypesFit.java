/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
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
 * Coco that checks whether the used provision and extension points fit to the
 * binding type (as, wfr, gen).
 *
 * @author Jerome Pfeiffer
 */
public class BindingTypesFit implements LanguageFamilyASTLanguageFamilyCoCo {

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
            if (!ppLanguageComponent.isPresent()) {
              return;
            }

            Optional<LanguageComponentSymbol> epLanguageComponent = Optional.empty();
            if (epLanguageComponentName.isPresent()) {
              epLanguageComponent = node.getEnclosingScope()
                  .resolveLanguageComponent(epLanguageComponentName.get().toString());
            }

            if (bindingElement.isProduction()) {
              if (!ppLanguageComponent.get().getAstNode().getGrammarProvisionPoint(ppName).isPresent()) {
                Log.error("LF004 bound provision point " + ppName
                        + " is used in AS binding but is no AS provision point.",
                    bindingElement.get_SourcePositionStart());
              }
              if (epLanguageComponent.isPresent()
                  && !epLanguageComponent.get().getAstNode().getGrammarExtensionPoint(epName).isPresent()) {
                Log.error("LF005 bound extension point " + epName
                        + " is used in AS binding but is no AS extension point.",
                    bindingElement.get_SourcePositionStart());
              }

            } else if (bindingElement.isWfrs()) {
              if (!ppLanguageComponent.get().getAstNode().getWfrSetDefinition(ppName).isPresent()) {
                Log.error("LF006 bound element with name " + ppName
                        + " is used in WFR binding but is no WFR set.",
                    bindingElement.get_SourcePositionStart());
              }

              if (epLanguageComponent.isPresent()) {
                if (!epLanguageComponent.get().getAstNode().getWfrSetDefinition(epName).isPresent()) {
                  Log.error("LF007 bound element with name " + epName
                          + " is used in WFR binding but is no WFR set.",
                      bindingElement.get_SourcePositionStart());
                }
              }

            } else if (bindingElement.isGen()) {
              if (!ppLanguageComponent.get().getAstNode().getGENProvisionPoint(ppName).isPresent()) {
                Log.error("LF008 bound provision point " + ppName
                        + " is used in GEN binding but is no GEN provision point.",
                    bindingElement.get_SourcePositionStart());
              }

              if (epLanguageComponent.isPresent()) {
                if (!epLanguageComponent.get().getAstNode().getGENExtensionPoint(epName).isPresent()) {
                  Log.error("LF009 bound extension point " + epName
                          + " is used in GEN binding but is no GEN extension point.",
                      bindingElement.get_SourcePositionStart());
                }
              }
            }
          }
        }
      }
    }
  }
}
