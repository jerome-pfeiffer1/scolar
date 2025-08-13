/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationconfiguration.cocos;

import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._cocos.CustomizationConfigurationASTCustomizationConfigurationCoCo;

/**
 * @author (last commit) Mutert
 */
public class ReferencedLanguageComponentsAreImported implements
    CustomizationConfigurationASTCustomizationConfigurationCoCo {

  @Override
  public void check(ASTCustomizationConfiguration node) {
//    final List<String> importedLanguageComponentNames =
//        node.getComponentDeclarationBlock()
//            .getComponentsList()
//            .stream()
//            .map(ASTComponentDeclaration::getName)
//            .collect(Collectors.toList());
//
//    for (final ASTComponentBinding languageComponentBinding : node.getComponentBindingList()) {
//      final String referencedLanguageComponent = languageComponentBinding.getComponentName();
//
//      if (!importedLanguageComponentNames.contains(referencedLanguageComponent)) {
//        Log.error(
//            String.format(
//                "CC002 The referenced language component '%s' in the bindings of customization " +
//                    "configuration '%s' is not imported.",
//                referencedLanguageComponent, node.getName()),
//            languageComponentBinding.get_SourcePositionStart());
//      }
//    }
  }
}
