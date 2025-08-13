/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily._cocos.util;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import languagefamily._ast.ASTFeatureDeclaration;

import java.util.List;
import java.util.Optional;

/**
 * Utility class
 *
 * @author Pfeiffer
 * @author Mutert
 */
public class Util {

  /**
   * Determine the realizing languageComponent name for the given feature from the
   * given feature declarations.
   *
   * @param feature Name of the feature for which the realizing language component name
   *                is to be determined
   * @param featureDeclarations Feature declarations
   * @return {@link Optional#empty()} if there is no declaration for the feature.
   * Optional containing the name of the realizing languageComponent, otherwise.
   */
  public static Optional<ASTMCQualifiedName> getRealizingLanguageComponentName(
      String feature,
      List<ASTFeatureDeclaration> featureDeclarations) {
    for (ASTFeatureDeclaration featureDeclaration : featureDeclarations) {
      if (featureDeclaration.getName().equals(feature)) {
        return Optional.ofNullable(featureDeclaration.getRealizingComponentName());
      }
    }
    return Optional.empty();
  }
}
