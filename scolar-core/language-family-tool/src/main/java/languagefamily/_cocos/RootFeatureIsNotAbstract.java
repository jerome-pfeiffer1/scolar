/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily._cocos;

import de.se_rwth.commons.logging.Log;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._symboltable.FeatureDeclarationSymbol;

import java.util.Optional;

/**
 * Checks whether the root feature is abstract.
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public class RootFeatureIsNotAbstract implements LanguageFamilyASTLanguageFamilyCoCo {

  @Override
  public void check(ASTLanguageFamily node) {
    String rootName = node.getFeatureDiagram().getRootFeature();
    Optional<FeatureDeclarationSymbol> rootFeatureDeclaration =
        node.getSpannedScope().resolveFeatureDeclaration(rootName);

    if (rootFeatureDeclaration.isPresent()) {
      boolean isAbstract =
          !rootFeatureDeclaration.get()
              .getAstNode()
              .isPresentRealizingComponentName();
      if (isAbstract) {
        Log.error("LF016 Root feature " + rootName + " must not be abstract.",
            rootFeatureDeclaration.get().getAstNode().get_SourcePositionStart());
      }
    }
  }

}
