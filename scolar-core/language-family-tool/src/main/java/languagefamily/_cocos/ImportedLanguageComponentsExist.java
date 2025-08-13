/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily._cocos;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily._ast.ASTFeatureDeclaration;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._symboltable.ILanguageFamilyScope;
import languagefamily._symboltable.LanguageFamilyScope;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks whether all imported language components exist.
 *
 * @author Mutert
 * @author Pfeiffer
 */
public class ImportedLanguageComponentsExist implements LanguageFamilyASTLanguageFamilyCoCo {

  @Override
  public void check(ASTLanguageFamily node) {
    final ILanguageFamilyScope globalScope = node.getEnclosingScope();


    for (final ASTFeatureDeclaration feature :
            node.getFeaturesList()) {
      String qName = feature.getRealizingComponentName().getQName();

      Optional<LanguageComponentSymbol> ppLanguageComponent =
          globalScope.resolveLanguageComponent(qName);

      if (!ppLanguageComponent.isPresent()) {
        Log.error(String.format(
            "LF014 The language component '%s' imported in language family '%s' " +
                "does not exist.",
                        qName, node.getName()),
                feature.get_SourcePositionStart());
      }
    }
  }
}
