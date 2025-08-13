package customizationconfiguration.cocos;

import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._ast.ASTRootConfiguration;
import customizationconfiguration._cocos.CustomizationConfigurationASTCustomizationConfigurationCoCo;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._symboltable.LanguageComponentSymbol;

import java.util.Optional;

public class ReferencedStartPPsExist
    implements CustomizationConfigurationASTCustomizationConfigurationCoCo {

  @Override
  public void check(ASTCustomizationConfiguration node) {
//    final ASTRootConfiguration RootConfiguration = node.getRootConfiguration();
//
//    final String referencedComponentName = node.getLanguageComponent().toString();
//    if(!node.getEnclosingScopeOpt().isPresent()){
//      return; // TODO Better error handling
//    }
//    final Optional<LanguageComponentSymbol> referencedComponent =
//        node.getEnclosingScopeOpt().get()
//        .resolve(referencedComponentName, LanguageComponentSymbol.KIND);
//
//    if(!referencedComponent.isPresent()){
//      return;
//    }
//
//    for (final String selectedAsPpName : node.getSelectedAsPpNames()) {
//      if (!referencedComponent.get().isPointTypeAS(selectedAsPpName)) {
//        Log.error(String.format(
//            "CC015 The selected abstract syntax provision point %s is not " +
//                "an abstract syntax provision point in the component %s",
//            selectedAsPpName,
//            referencedComponent.get().getFullName()),
//            RootConfiguration.get_SourcePositionStart());
//      }
//    }
//
//    for (final String selectedGenPPName : node.getSelectedGenPpNames()) {
//      if (!referencedComponent.get().isPointTypeGEN(selectedGenPPName)) {
//        Log.error(String.format(
//            "CC016 The selected generator provision point %s " +
//                "is not a generator provision point in the component %s",
//            selectedGenPPName,
//            referencedComponent.get().getFullName()),
//            RootConfiguration.get_SourcePositionStart());
//      }
//    }
//
//    for (final String wfrSetName : node.getSelectedWfrSetNames()) {
//      if(!referencedComponent.get().isPointTypeWFR(wfrSetName)) {
//        Log.error(String.format(
//            "CC017 The selected well-formedness rule set %s " +
//                "is not a wfr set in the component %s",
//            wfrSetName,
//            referencedComponent.get().getFullName()),
//            RootConfiguration.get_SourcePositionStart());
//      }
//    }
  }
}
