/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily._cocos;

import de.se_rwth.commons.logging.Log;
import languagefamily._ast.ASTFeatureDeclaration;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.util.FeatureNameCollectorVisitor;

import java.util.List;
import java.util.Set;

/**
 * Checks whether the declared features are used. If not, it throws a warning.
 *
 * @author Jerome Pfeiffer
 */
public class AllDeclaredFeaturesUsed implements LanguageFamilyASTLanguageFamilyCoCo {

  @Override
  public void check(ASTLanguageFamily node) {

    List<String> usedFeatureNames = node.getFeatureDiagram().getAllFeatures();

    for (ASTFeatureDeclaration featureDeclaration : node.getFeaturesList()) {
      if (!usedFeatureNames.contains(featureDeclaration.getName())) {
        Log.warn("LF002 feature " + featureDeclaration.getName() + " is declared but not used!",
            featureDeclaration.get_SourcePositionStart());
      }
    }
  }
}
