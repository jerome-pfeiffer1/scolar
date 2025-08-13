/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationconfiguration.cocos;

import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._cocos.CustomizationConfigurationASTCustomizationConfigurationCoCo;

/**
 * @author Michael Mutert
 */
public class ReferencedElementsExist implements
    CustomizationConfigurationASTCustomizationConfigurationCoCo {

  @Override
  public void check(ASTCustomizationConfiguration node) {
//    final Scope globalScope = node.getEnclosingScope();
//    // TODO Adapt to changes
//    // Load language family and check if it actually exists
//    final ASTQualifiedName referencedComponent = node.getLanguageComponent();
//    final Optional<LanguageComponentSymbol> languageComponentSymbol =
//        globalScope.resolve(referencedComponent.toString(), LanguageComponentSymbol.KIND);
//
//    if (!languageComponentSymbol.isPresent()
//        || !languageComponentSymbol.get().getLanguageComponentNode().isPresent()) {
//      Log.error(
//          String.format(
//              "CC003 The language component '%s' referenced in customization " +
//                  "configuration '%s' could not be loaded, or does not exist.",
//              referencedComponent.toString(), node.getName()),
//          referencedComponent.get_SourcePositionStart());
//      return;
//    }
//
//    final ASTLanguageComponent astLC =
//        languageComponentSymbol.get().getLanguageComponentNode().get();
//
//    final Set<String> cpNames = astLC.getExtensionPointNames();
//
//    checkUsedParametersExist(node, astLC);
//
//    for (final ASTComponentBinding languageComponentBinding : node.getComponentBindingList()) {
//      // Check that the referenced CP exists in the CI
//
//      if (languageComponentBinding.getEpNameOpt().isPresent()) {
//
//        if (!cpNames.contains(languageComponentBinding.getEpNameOpt().get())) {
//          Log.error(
//              String.format(
//                  "CC005 The customization point '%s' bound in customization " +
//                      "configuration '%s' does not exist in the customization " +
//                      "interface of the referenced language family '%s'.",
//                  languageComponentBinding.getEpNameOpt().get(),
//                  node.getName(),
//                  referencedComponent),
//              languageComponentBinding.get_SourcePositionStart());
//        }
//
//        checkProvisionPointsExistAndTypeFits(node, globalScope, languageComponentBinding);
//
//        final Optional<ASTRequiredExtension> extensionPoint =
//            astLC.getExtensionPoint(languageComponentBinding.getEpNameOpt().get());
//        if (extensionPoint.isPresent()) {
//          checkEPHasCorrectType(languageComponentBinding, extensionPoint.get());
//        }
//
//      } else {
//        // No EP name given
//        checkCorrectPresenceOfEPName(languageComponentBinding);
//      }
//    }
  }

//  private void checkUsedParametersExist(
//      ASTCustomizationConfiguration cc,
//      ASTLanguageComponent languageComponent) {
//
//    final Set<String> parameterNames = languageComponent.getParameterNames();
//
//    for (final ASTParameterAssignment parameterAssignment : cc.getParameterAssignmentList()) {
//      if (!parameterNames.contains(parameterAssignment.getName())) {
//        Log.error(
//            String.format(
//                "CC004 The parameter '%s' bound in customization configuration '%s' " +
//                    "does not exist in the customization interface of the " +
//                    "referenced language family '%s'.",
//                parameterAssignment.getName(), cc.getName(), cc.getLanguageComponent()),
//            parameterAssignment.get_SourcePositionStart());
//      }
//    }
//  }
//
//  private void checkCorrectPresenceOfEPName(ASTComponentBinding languageComponentBinding) {
//    switch (languageComponentBinding.getType()) {
//      case AS:
//        Log.error("CC013 Abstract syntax bindings always require an EP to bind to.",
//            languageComponentBinding.get_SourcePositionStart());
//        break;
//      case GEN:
//      Log.error("CC014 Transformation bindings always require an EP to bind to.",
//          languageComponentBinding.get_SourcePositionStart());
//    }
//  }
//
//  private void checkEPHasCorrectType(
//          ASTComponentBinding languageComponentBinding,
//          ASTRequiredExtension epDefinition) {
//
//    switch (languageComponentBinding.getType()) {
//      case AS:
//      if (!(epDefinition instanceof ASTRequiredGrammarExtension)) {
//        Log.error(
//            "CC010 The referenced customization point is not an abstract " +
//                "syntax point, but it is required to be one by the type of the binding.",
//            languageComponentBinding.get_SourcePositionStart());
//      }
//      break;
//      case GEN:
//      if (!(epDefinition instanceof ASTRequiredGenExtension)) {
//        Log.error(
//            "CC011 The referenced customization point is not an transformation " +
//                "point, but it is required to be one by the type of the binding.",
//            languageComponentBinding.get_SourcePositionStart());
//      }
//      break;
//    }
//  }
//
//  private void checkProvisionPointsExistAndTypeFits(
//      ASTCustomizationConfiguration node,
//      Scope globalScope,
//      ASTComponentBinding languageComponentBinding) {
//
//    // Check that the referenced CP fits the type of the binding
//
//    final String languageComponentName = languageComponentBinding.getComponentName();
//    final String languageComponentFQName =
//        node.getComponentFullName(languageComponentName).orElse("");
//    Optional<LanguageComponentSymbol> ppLanguageComponent =
//        globalScope.resolve(languageComponentFQName, LanguageComponentSymbol.KIND);
//    if(!ppLanguageComponent.isPresent()) {
//      return; // Existence of language component is checked in another coco.
//    }
//
//    final String ppName = languageComponentBinding.getPpName();
//
//    final boolean pointTypeAS = ppLanguageComponent.get().isPointTypeAS(ppName);
//      final boolean pointTypeGEN = ppLanguageComponent.get().isPointTypeGEN(ppName);
//      final boolean pointTypeWFR = ppLanguageComponent.get().isPointTypeWFR(ppName);
//
//    if (!pointTypeAS && !pointTypeGEN && !pointTypeWFR) {
//      Log.error(
//          String.format(
//              "CC006 The referenced provision point '%s.%s' in the binding in " +
//                  "customization configuration '%s' does not exist",
//              ppLanguageComponent, ppName, node.getName()),
//          languageComponentBinding.get_SourcePositionStart());
//    } else if (languageComponentBinding.getType().equals(ASTCCBindingType.AS)) {
//      if (!pointTypeAS) {
//        Log.error(
//            "CC007 The type of the binding is abstract syntax, " +
//                "but the referenced provision point is of another type.",
//            languageComponentBinding.get_SourcePositionStart());
//      }
//    } else if (languageComponentBinding.getType().equals(ASTCCBindingType.GEN)) {
//      if (!pointTypeGEN) {
//        Log.error(
//            "CC008 The type of the binding is transformation, " +
//                "but the referenced provision point is of another type.",
//            languageComponentBinding.get_SourcePositionStart());
//      }
//    } else if (languageComponentBinding.getType().equals(ASTCCBindingType.WFR)) {
//      if (!pointTypeWFR) {
//        Log.error(
//            "CC009 The type of the binding is well-formedness, " +
//                "but the referenced provision point is of another type.",
//            languageComponentBinding.get_SourcePositionStart());
//      }
//    }
//  }
}
