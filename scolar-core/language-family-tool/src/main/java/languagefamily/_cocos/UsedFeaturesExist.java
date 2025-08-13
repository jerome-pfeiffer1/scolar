package languagefamily._cocos;

import de.se_rwth.commons.logging.Log;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.util.FeatureNameCollectorVisitor;
import languagefamily._symboltable.FeatureDeclarationSymbol;

import java.util.List;
import java.util.Set;

/**
 * Features which are used in the Feature Diagram exist in the model.
 *
 * @author Jerome Pfeiffer
 */
public class UsedFeaturesExist implements LanguageFamilyASTLanguageFamilyCoCo {

  @Override
  public void check(ASTLanguageFamily node) {

    List<String> featureNames = node.getFeatureDiagram().getAllFeatures();
    for (String name : featureNames) {
      if (!node.getSpannedScope().resolveFeatureDeclaration(name).isPresent()) {
        Log.error("LF001 Feature " + name + " is used but not declared!",
            node.get_SourcePositionStart());
      }
    }
  }
}
