package languagecomponentbase.cocos;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTImplication;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTProvidedGrammarExtension;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

import java.util.Optional;

/**
 * Ensures that only abstract syntax provision points are the sources of implications
 * or the implications are defined between a generator PP and generator EP.
 *
 * @author Michael Mutert
 */
public class ImplicationSourcesAndTargetsOfCorrectType
    implements LanguageComponentBaseASTLanguageComponentCoCo {

  @Override
  public void check(ASTLanguageComponent node) {

    for (ASTImplication implication : node.getImplications()) {
      final String source = implication.getSource();
      final Optional<ASTProvidedGrammarExtension> asProvidesPoint =
          node.getGrammarProvisionPoint(source);

      if(!asProvidesPoint.isPresent()){
        if(node.getGENProvisionPoint(source).isPresent()) {

          // Check if targets are GEN EPs
          for (final String targetEP : implication.getTargetList()) {
            if (!node.getGENExtensionPoint(targetEP).isPresent()) {
              Log.error(
                  String.format(
                      "LC006 The source %s of the implication is an generator " +
                          "provision point. The target %s is not an generator " +
                          "extension point.",
                      source, targetEP),
                  implication.get_SourcePositionStart());
            }
          }
        } else {
          Log.error(
              String.format(
                  "LC006 The source %s of the implication is not an abstract syntax " +
                      "or generator provision point.", source),
              implication.get_SourcePositionStart());
        }
      }
    }
  }
}
