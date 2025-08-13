/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily._cocos;

import com.sun.source.tree.Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily._ast.ASTBindingElement;
import languagefamily._ast.ASTFeatureDeclaration;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.util.Util;
import languagefamily._symboltable.LanguageFamilyGlobalScope;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks whether the used variation points exist.
 *
 * @author Jerome Pfeiffer
 */
public class UsedVPExist implements LanguageFamilyASTLanguageFamilyCoCo {

  /**
   * @see languagefamily._cocos.LanguageFamilyASTLanguageFamilyCoCo#check(languagefamily._ast.ASTLanguageFamily)
   */
  @Override
  public void check(ASTLanguageFamily node) {


    for (ASTFeatureDeclaration featureDeclaration : node.getFeaturesList()) {
      if (featureDeclaration.isPresentRealizingComponentName()) {
        ASTMCQualifiedName ppLanguageComponentName = featureDeclaration.getRealizingComponentName();

        for (ASTBindingElement bindingElement : featureDeclaration.getBindingElementList()) {
          String ppName = bindingElement.getProvidedElement();
          if (bindingElement.isPresentEpFeature()) {
            Optional<ASTMCQualifiedName> epLanguageComponentName = Util
                .getRealizingLanguageComponentName(
                    bindingElement.getEpFeature(),
                    node.getFeaturesList());
            if (epLanguageComponentName.isPresent()) {
              doesVPExist(epLanguageComponentName.get(),
                  bindingElement.getEpName(), (LanguageFamilyGlobalScope) node.getEnclosingScope().getEnclosingScope());
            }
          }

          doesVPExist(ppLanguageComponentName, ppName, (LanguageFamilyGlobalScope) node.getEnclosingScope().getEnclosingScope());
        }
      }
    }
  }

  /**
   * Checks whether the passed variation point name exists in the given
   * languageComponent.
   *
   * @param languageComponentName
   * @param vpName
   */
  private void doesVPExist(
      ASTMCQualifiedName languageComponentName,
      String vpName,
      LanguageFamilyGlobalScope s) {

    Optional<LanguageComponentSymbol> languageComponent = s
        .resolveLanguageComponent(languageComponentName.toString());
    if (!languageComponent.isPresent()) {
      return; // The existence of the language component is checked in another coco
    }
    final boolean pointTypeEP = languageComponent.get().getAstNode().getExtensionPoint(vpName).isPresent();
    final boolean pointTypePP = languageComponent.get().getAstNode().getProvisionPoint(vpName).isPresent();
    final boolean pointTypeWFR = languageComponent.get().getAstNode().getWfrSetDefinition(vpName).isPresent();
    if (!pointTypeEP && !pointTypePP && !pointTypeWFR) {
      Log.error("LF010 the VP " + vpName + " is referenced but does not exist!",
          languageComponentName.get_SourcePositionStart());
    }

  }

}
