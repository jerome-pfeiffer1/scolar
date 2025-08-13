/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationconfiguration.cocos;

import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._cocos.CustomizationConfigurationASTCustomizationConfigurationCoCo;

/**
 * TODO
 *
 * @author (last commit) Mutert
 */
public class ImportedLanguageComponentsExist implements
    CustomizationConfigurationASTCustomizationConfigurationCoCo {

  @Override
  public void check(ASTCustomizationConfiguration node) {
//    final Scope globalScope = node.getEnclosingScope();
//
//    final ASTComponentDeclarationBlock block = node.getComponentDeclarationBlock();
//    for (final ASTComponentDeclaration componentDecl : block.getComponentsList()) {
//
//      final ASTQualifiedName languageComponentFQName = componentDecl.getQualifiedName();
//      Optional<LanguageComponentSymbol> ppLanguageComponent =
//          globalScope.resolve(languageComponentFQName.toString(), LanguageComponentSymbol.KIND);
//
//      if (!ppLanguageComponent.isPresent()) {
//        Log.error(String.format(
//            "CC001 The language component '%s' imported in customization configuration '%s' " +
//                "for language component '%s' does not exist.",
//            languageComponentFQName.toString(),
//            node.getName(),
//            node.getLanguageComponent().toString()),
//            componentDecl.get_SourcePositionStart());
//      }
//    }
  }
}
