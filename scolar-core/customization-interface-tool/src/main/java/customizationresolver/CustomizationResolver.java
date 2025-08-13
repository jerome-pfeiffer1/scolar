/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package customizationresolver;

import composition.ILanguageComponentComposer;
import customizationconfiguration.CustomizationConfigurationProcessor;
import customizationconfiguration._ast.*;
import customizationconfiguration._symboltable.CustomizationConfigurationSymbol;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.*;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import util.Binding;
import util.Binding.BindingType;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Resolves the configuration of the customization interface.
 *
 * @author Mutert
 * @author Pfeiffer
 */
public class CustomizationResolver {
  
  private final ILanguageComponentComposer languageComponentComposer;
  
  private final CustomizationConfigurationProcessor ccProcessor;
  
  private final Path outputPath;
  
  public CustomizationResolver(
      ILanguageComponentComposer languageComponentComposer,
      MCPath modelPath,
      Path outputPath) {
    
    this.languageComponentComposer = languageComponentComposer;
    this.outputPath = outputPath;
    this.ccProcessor = new CustomizationConfigurationProcessor(
         modelPath);
  }
  
  /**
   * For the given customization configuration, resolves all bindings
   * and assignments for parameters, as well as the contained root
   * configuration. Outputs the resulting language component.
   * 
   * @param customizationConfig config for customization
   * @return Optional of language component if customization config can be resolved
   */
  public Optional<ASTLanguageComponentCompilationUnit> resolveCustomizationConfig(
      String customizationConfig) {
    Optional<CustomizationConfigurationSymbol> ccSymbol = ccProcessor
        .loadCCSymbol(customizationConfig);
    if (ccSymbol.isPresent()) {
      ASTCustomizationConfiguration customizationConfiguration = ccSymbol.get()
          .getAstNode();
      ASTMCQualifiedName languageComponentName = customizationConfiguration.getLanguageComponent();
      Optional<LanguageComponentSymbol> languageComponent = languageComponentComposer
          .getLanguageComponentProcessor()
          .loadLanguageComponentSymbol(languageComponentName.toString());
      final ASTLanguageComponentCompilationUnit compCompUnit = (ASTLanguageComponentCompilationUnit) languageComponent
          .get()
          .getEnclosingScope()
          .getAstNode()
          ;
      return resolveCustomization(compCompUnit, customizationConfiguration);
    }
    return Optional.empty();
  }
  
  /**
   * For the given customization configuration, tries to resolve all bindings
   * and assignments for parameters, as well as the contained root
   * configuration. Outputs the result to the output path of the Resolver.
   *
   * @param languageComponent The language component for which to resolve the
   * customization
   * @param customizationConfig The configuration to use.
   * @return The customized language component, if customization was possible
   */
  public Optional<ASTLanguageComponentCompilationUnit> resolveCustomization(
      ASTLanguageComponentCompilationUnit languageComponent,
      ASTCustomizationConfiguration customizationConfig) {
    
    // Precondition: The customization configuration has to be intended
    // for the given language component
    final String componentNameToCustomize = customizationConfig.getLanguageComponent().toString();
    final String componentName = Names.constructQualifiedName(
        languageComponent.getPackageList(),
        languageComponent.getLanguageComponent().getName());
    // Name of the new composed language project should be the name of the configuration file
    final String composedProjectName = customizationConfig.getName();
    
    if (!componentNameToCustomize.equals(componentName)) {
      Log.warn(String.format(
          "The customization configuration %s is defined for component%s. " +
              "The language component given for customization is %s",
          customizationConfig.getName(),
          componentNameToCustomize,
          languageComponent.getLanguageComponent().getName()));
      return Optional.empty();
    }

    ASTLanguageComponentCompilationUnit composedComponent = languageComponent.deepClone();
    
    composedComponent = applyBindings(composedComponent, customizationConfig);
    
    // bind parameters
    handleParameterAssignment(composedComponent.getLanguageComponent(), customizationConfig);
    
    // Remove the unselected wfr sets
    final List<String> selectedWfrSetNames = customizationConfig.getSelectedWfrSetNames();
    final Set<ASTWfrSetDefinition> unselectedWfrSetsOfStartComp = languageComponent
        .getLanguageComponent().getWfrSetDefinitions()
        .stream()
        .filter(e -> !selectedWfrSetNames.contains(e.getName()))
        .collect(Collectors.toSet());
    composedComponent.getLanguageComponent().removeAllLanguageComponentElements(unselectedWfrSetsOfStartComp);
    
    // The unused sets have already been removed and are not considered in
    // determining the referenced rule names.
    final Set<String> referencedRuleNames = composedComponent.getLanguageComponent().getWfrSetDefinitions()
        .stream()
        .flatMap(s -> s.getWfrDefinitionList().stream())
        .map(s -> s.getWfrReference().toString())
        .collect(Collectors.toSet());
    
    final Set<ASTParameter> unusedParameters = composedComponent.getLanguageComponent().getParameters()
        .stream()
        .filter(ASTParameter::isWfr)
        .filter(p -> !referencedRuleNames.contains(p.getReference().toString()))
        .collect(Collectors.toSet());
    composedComponent.getLanguageComponent().removeAllLanguageComponentElements(unusedParameters);
    
    Set<ASTProvidedGenExtension> selectedGenPPs = new HashSet<>();
    for (final String ppName : customizationConfig.getSelectedGenPpNames()) {
      selectedGenPPs.add(composedComponent.getLanguageComponent().getGENProvisionPoint(ppName).get());
    }
    Set<ASTProvidedGrammarExtension> selectedAsPPs = new HashSet<>();
    for (final String ppName : customizationConfig.getSelectedAsPpNames()) {
      selectedAsPPs.add(composedComponent.getLanguageComponent().getGrammarProvisionPoint(ppName).get());
    }
    
    // Handle the rule pp selection
    removeUnselectedGrammarPPsAndImpliedEPs(composedComponent.getLanguageComponent(), selectedAsPPs);
    
    // Handle the generator pp selection
    removeUnselectedGeneratorPPsAndImpliedEPs(composedComponent.getLanguageComponent(), selectedGenPPs);
    
    // Remove implications for removed elements
    Set<String> selectedASPPNames = selectedAsPPs.stream().map(ASTProvidedGrammarExtension::getName)
        .collect(Collectors.toSet());
    Set<String> selectedGENPPNames = selectedGenPPs.stream().map(ASTProvidedGenExtension::getName)
        .collect(Collectors.toSet());
    final Set<ASTImplication> implicationsToRemove = composedComponent.getLanguageComponent().getImplications()
        .stream()
        .filter(i -> !selectedASPPNames.contains(i.getSource())
            && !selectedGENPPNames.contains(i.getSource()))
        .collect(Collectors.toSet());
    composedComponent.getLanguageComponent().removeAllLanguageComponentElements(implicationsToRemove);
    
    languageComponentComposer.getArtifactComposer()
        .addSelectedWfrSets(composedComponent.getLanguageComponent(), selectedWfrSetNames);
    
    composedComponent.getLanguageComponent().setName(composedComponent.getLanguageComponent().getName().concat("Customized"));
    ASTLanguageComponentCompilationUnit customizedLanguageComponent = LanguageComponentBaseMill
        .languageComponentCompilationUnitBuilder()
        .setLanguageComponent(composedComponent.getLanguageComponent())
        .addPackage(Names.constructQualifiedName(languageComponent.getPackageList()))
        .build();
    
    // output customized component
    languageComponentComposer.getLanguageComponentProcessor()
        .printLanguageComponent(composedProjectName, customizedLanguageComponent, outputPath);
    
    // output newly created language components artifacts
    languageComponentComposer.getArtifactComposer().outputResult(composedProjectName);

    return Optional.of(customizedLanguageComponent);
  }
  
  /**
   * Apply bindings of customization configuration
   * 
   * @param composedComponent
   * @param customizationConfig
   */
  private ASTLanguageComponentCompilationUnit applyBindings(ASTLanguageComponentCompilationUnit composedComponent, ASTCustomizationConfiguration customizationConfig) {
    // bind language components
    // Sort the bindings by the pp components name and apply all at once
    Map<String, List<Binding>> componentToBindings = convertBindings(customizationConfig);
    
    ASTLanguageComponentCompilationUnit res = composedComponent.deepClone();
    
    for (final String ppCompName : componentToBindings.keySet()) {
      
      Optional<LanguageComponentSymbol> ppLanguageComponentSymbol = languageComponentComposer
          .getLanguageComponentProcessor()
          .loadLanguageComponentSymbol(ppCompName);
      
      if (ppLanguageComponentSymbol.isEmpty()
          || !ppLanguageComponentSymbol.get().isPresentAstNode()) {
        Log.error(String.format("Could not load language component '%s'", ppCompName));
        return null;
      }
      
      final ASTCCompilationUnitA astcCompilationUnitA = (ASTCCompilationUnitA) ppLanguageComponentSymbol.get().getEnclosingScope().getAstNode();
      ASTLanguageComponentCompilationUnit ppComponent = astcCompilationUnitA.getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit();
      // Compose the language components
      res = languageComponentComposer.composeLanguageComponent(
          ppComponent, res, componentToBindings.get(ppCompName));
    }
    return res;
  }
  
  private void removeUnselectedGrammarPPsAndImpliedEPs(
      ASTLanguageComponent composedRootComponent,
      Set<ASTProvidedGrammarExtension> selectedAsPPs) {
    
    // Remove the Provision Point
    final Set<ASTProvidedGrammarExtension> unselectedAsPPs = composedRootComponent
        .getGrammarProvisionPoints()
        .stream()
        .filter(pp -> !selectedAsPPs.contains(pp))
        .collect(Collectors.toSet());
    composedRootComponent.removeAllLanguageComponentElements(unselectedAsPPs);
    
    // Remove all implied Extension points not implied by others
    final Set<String> selectedASImpliedEPs = selectedAsPPs.stream()
        .flatMap(pp -> composedRootComponent.getImpliedEPs(pp.getName()).stream())
        .collect(Collectors.toSet());
    
    for (final ASTProvidedGrammarExtension unselectedAsPP : unselectedAsPPs) {
      removeImpliedEPsForUnselectedPoint(composedRootComponent, selectedASImpliedEPs,
          unselectedAsPP.getName());
    }
  }
  
  private void removeUnselectedGeneratorPPsAndImpliedEPs(
      ASTLanguageComponent composedRootComponent,
      Set<ASTProvidedGenExtension> selectedGenPPs) {
    
    // Remove the Provision Point
    final Set<ASTProvidedGenExtension> unselectedGenPPs = composedRootComponent.getGENProvisionPoints()
        .stream()
        .filter(pp -> !selectedGenPPs.contains(pp))
        .collect(Collectors.toSet());
    composedRootComponent.removeAllLanguageComponentElements(unselectedGenPPs);
    
    // Remove all implied Extension points not implied by others
    final Set<String> selectedGENImpliedEPs = selectedGenPPs.stream()
        .flatMap(pp -> composedRootComponent.getImpliedEPs(pp.getName()).stream())
        .collect(Collectors.toSet());
    
    for (final ASTProvidedGenExtension unselectedGenPP : unselectedGenPPs) {
      removeImpliedEPsForUnselectedPoint(composedRootComponent, selectedGENImpliedEPs,
          unselectedGenPP.getName());
    }
  }
  
  /**
   * @param composedRootComponent The component to modify.
   * @param selectedPointsImpliedEPs The names of all extension points that are
   * implied by a selected point
   * @param unselectedPointName The name of the provision point that was not
   * selected.
   */
  private void removeImpliedEPsForUnselectedPoint(
      ASTLanguageComponent composedRootComponent,
      Set<String> selectedPointsImpliedEPs,
      String unselectedPointName) {
    
    final Collection<String> unselectedGENImpliedEPs = composedRootComponent
        .getImpliedEPs(unselectedPointName);
    unselectedGENImpliedEPs.removeAll(selectedPointsImpliedEPs);
    
    for (final String impliedEP : unselectedGENImpliedEPs) {
      final Optional<ASTRequiredExtension> extensionPoint = composedRootComponent
          .getExtensionPoint(impliedEP);
      extensionPoint.ifPresent(composedRootComponent::removeLanguageComponentElement);
    }
  }
  
  /**
   * Converts the bindings in the given customization configuration to the
   * {@link Binding} class, whilst collecting them according to the source
   * component name.
   * 
   * @param customizationConfig The configuration for which to convert the
   * bindings.
   * @return A map from component name to the set of converted bindings
   */
  private Map<String, List<Binding>> convertBindings(
      ASTCustomizationConfiguration customizationConfig) {
    
    Map<String, List<Binding>> componentToBindings = new HashMap<>();
    for (final ASTComponentBinding binding : customizationConfig.getComponentBindingList()) {
      final String componentName = binding.getComponentName().toString();
      if (!componentToBindings.containsKey(componentName)) {
        componentToBindings.put(componentName, new ArrayList<>());
      }
      
      final List<Binding> bindings = componentToBindings.get(componentName);
      final String ppName = binding.getPpName();
      final String epName = binding.getEpName();
      switch (binding.getType()) {
        case AS:
          bindings.add(new Binding(BindingType.AS, ppName, epName));
          break;
        case GEN:
          bindings.add(new Binding(BindingType.GEN, ppName, epName));
          break;
        case WFR:
          bindings.add(new Binding(BindingType.WFR, ppName, epName));
          break;
      }
    }
    return componentToBindings;
  }
  
  protected ASTLanguageComponent handleParameterAssignment(
      ASTLanguageComponent languageComponent, ASTCustomizationConfiguration cc) {
    
    for (ASTParameterAssignment parameterAssignment : cc.getParameterAssignmentList()) {
      final String paramName = parameterAssignment.getName();
      final ASTExpression paramValue = parameterAssignment.getValue();
      
      // Search for the name of the context condition which has the parameter
      final Optional<ASTParameter> param = languageComponent.getParameter(paramName);
      
      if (param.isEmpty()) {
        break;
      }

      String printedValue = ExpressionsBasisMill.prettyPrint(paramValue, false);

      languageComponentComposer.getArtifactComposer().setParameter(languageComponent, param.get(),
          printedValue);
      
      // The parameter is then no longer required and dismissed from the
      // component
      removeParameter(paramName, languageComponent);
    }
    
    return languageComponent;
  }
  
  private void removeParameter(String paramName, ASTLanguageComponent epLanguageComponent) {
    
    Set<ASTParameter> paramDefs = epLanguageComponent.getParameters();
    epLanguageComponent.getLanguageComponentElementList().removeAll(paramDefs);
    paramDefs.removeIf(p -> p.getName().equals(paramName));
    epLanguageComponent.getLanguageComponentElementList().addAll(paramDefs);
  }
  
  /**
   * Loads the given language component and customization configuration and
   * applies the customization configuration to the component, if possible.
   *
   * @param qualifiedComponentName The name of the component to customize
   * @param qualifiedCCName The name of the customization configuration
   * @return The customized component, if present. Empty optional, otherwise.
   */
  public Optional<ASTLanguageComponentCompilationUnit> resolveCustomization(
      String qualifiedComponentName,
      String qualifiedCCName) {
    
    final Optional<LanguageComponentSymbol> componentSymbol = languageComponentComposer
        .getLanguageComponentProcessor()
        .loadLanguageComponentSymbol(qualifiedComponentName);
    
    if (componentSymbol.isEmpty()) {
      Log.debug(
          String.format(
              "The language component with the fq name %s could not be loaded",
              qualifiedComponentName),
          "LanguageFamilyProcessor#resolveCustomization");
      return Optional.empty();
    }
    
    final Optional<CustomizationConfigurationSymbol> ccSymbol = ccProcessor
        .loadCCSymbol(qualifiedCCName);
    if (ccSymbol.isEmpty()) {
      Log.debug(
          String.format(
              "The customization configuration with the fq name %s could not be loaded",
              qualifiedCCName),
          "LanguageFamilyProcessor#resolveCustomization");
      return Optional.empty();
    }
    
    final ASTCCompilationUnitA ccCompUnit = (ASTCCompilationUnitA) componentSymbol
        .get()
        .getEnclosingScope()
        .getAstNode();
    ASTLanguageComponentCompilationUnit compCompUnit = ccCompUnit.getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit();
    final ASTCustomizationConfiguration cc = ccSymbol.get().getAstNode();
    
    return this.resolveCustomization(compCompUnit, cc);
  }
  
  /**
   * Loads the given customization configuration and applies the customization
   * configuration to the referenced component, if possible.
   *
   * @param customizationConfigurationName The name of the customization
   * configuration to process.
   * @return The customized component, if possible. {@link Optional#empty()},
   * otherwise.
   */
  public Optional<ASTLanguageComponentCompilationUnit> resolveCustomization(
      String customizationConfigurationName) {
    final Optional<CustomizationConfigurationSymbol> ccSymbol = this.ccProcessor
        .loadCCSymbol(customizationConfigurationName);
    
    if (ccSymbol.isEmpty()) {
      return Optional.empty();
    }
    
    final String referencedComponentName = ccSymbol.get().getAstNode()
        .getLanguageComponent().toString();
    
    return this.resolveCustomization(referencedComponentName, customizationConfigurationName);
  }
}
