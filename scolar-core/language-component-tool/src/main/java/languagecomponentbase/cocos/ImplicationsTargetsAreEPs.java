package languagecomponentbase.cocos;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTImplication;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTRequiredExtension;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

import java.util.Optional;

/**
 * Ensures that the targets of implications are only extension points.
 *
 * @author Michael Mutert
 */
public class ImplicationsTargetsAreEPs implements LanguageComponentBaseASTLanguageComponentCoCo {

  @Override
  public void check(ASTLanguageComponent node) {

    for (ASTImplication implication : node.getImplications()) {
      for (String target : implication.getTargetList()) {
        final Optional<ASTRequiredExtension> extensionPoint =
            node.getExtensionPoint(target);
        if(!extensionPoint.isPresent()){
          Log.error(
              String.format(
                  "LC007 The target %s of the implication is not an " +
                      "extension point.", target),
              implication.get_SourcePositionStart());
        }
      }
    }
  }
}
