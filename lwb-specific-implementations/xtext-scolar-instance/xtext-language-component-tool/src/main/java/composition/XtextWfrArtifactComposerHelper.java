package composition;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTWfrDefinition;
import languagecomponentbase._ast.ASTWfrSetDefinition;
import util.Binding;
import util.ValidatorClassInfo;

import java.nio.file.Path;
import java.util.*;

/**
 * Composes Xtext validators by means of their validation methods. For every used grammar, all wfrs are always called.
 * For imported and redefined rules, the validator class of the composed language calls all the methods of validator classes of bound wfrs.
 * The methods are overwritten based on their naming scheme "check<WFRName>(<referencedRule> <referencedRuleToLowerCase>)".
 * TODO how to handle parameters?
 */
public class XtextWfrArtifactComposerHelper {


  private List<ValidatorClassInfo> infoList = new ArrayList<>();

  void composeWFR(
          ASTLanguageComponentCompilationUnit ppComponent,
          ASTLanguageComponentCompilationUnit epComponent,
          Collection<Binding> bindings,
          String composedComponentName,
          String composedGrammarName) {

    String superValidatorName = ppComponent.getLanguageComponent().getASReference() + "Validator";
    for (Binding binding : bindings) {
      List<ValidatorClassInfo> validatorInfos = getValidatorInfos(binding, ppComponent.getLanguageComponent(), superValidatorName);
      infoList.addAll(validatorInfos);
    }
  }

  public String outputComposedWFR(String composedComponentPackage, String composedGrammarName) {
    GeneratorSetup setup = new GeneratorSetup();
    GeneratorEngine generatorEngine = new GeneratorEngine(setup);
    String composedValidatorName = composedGrammarName + "Validator";
    StringBuilder generate = generatorEngine.generate("freemarker.WFRcomposition.ftl", null, composedComponentPackage, composedValidatorName, infoList);
    return generate.toString();

  }



  private List<ValidatorClassInfo> getValidatorInfos(Binding binding, ASTLanguageComponent ppComponent, String superValidatorName) {
    List<ValidatorClassInfo> validatorInfoList = new ArrayList<>();

    if (binding.getBindingType() == Binding.BindingType.WFR) {
      // limitation: only one validator per language in Xtext hence they are always all added to one class at artifact level
//          Optional<ASTWfrSetDefinition> wfrSetDefinitionRE = epComponent.getWfrSetDefinition(binding.getExtensionPoint());
      Optional<ASTWfrSetDefinition> wfrSetDefinitionPE = ppComponent.getWfrSetDefinition(binding.getProvisionPoint());
      if (wfrSetDefinitionPE.isPresent()) {
        ASTWfrSetDefinition wfrSetDefinition = wfrSetDefinitionPE.get();
        String referencedRule = wfrSetDefinition.getReferencedRule();
        for (ASTWfrDefinition astWfrDefinition : wfrSetDefinition.getWfrDefinitionList()) {
          ASTMCQualifiedName wfrMethodName = astWfrDefinition.getWfrReference();
          validatorInfoList.add(new ValidatorClassInfo(superValidatorName, wfrMethodName.getBaseName(), referencedRule));
        }
      }
    }
    return validatorInfoList;
  }


  public void addStartSet(String s) {
  }
}

