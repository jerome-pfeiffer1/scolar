/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily;

import static util.Binding.BindingType.AS;
import static util.Binding.BindingType.GEN;
import static util.Binding.BindingType.WFR;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import composition.ILanguageComponentComposer;
import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._ast.ASTFeatureTreeRule;
import de.monticore.featurediagram._ast.ASTGroupPart;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.*;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily._ast.*;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;
import languagefamily._visitor.LanguageFamilyTraverser;
import util.Binding;

/**
 * Visits the feature diagram tree an for selected child and parent features
 * composes the realizing languageComponents according to the given bindings.
 *
 * @author (last commit) Michael Mutert, Jerome Pfeiffer
 */
public class LanguageFamilyResolver implements LanguageFamilyTraverser {

  private Set<String> selectedFeatures;

  // Map from feature name -> feature definition
  /**
   * Maps the name of a feature to its realizing languageComponent and the
   * bindings.
   */
  private Map<String, ASTFeatureDeclaration> featureDeclarations = new HashMap<>();

  private Map<String, ASTLanguageComponentCompilationUnit> featureNameToTmpLanguageComponent;

  private Deque<String> parentFeatures;

  private ILanguageComponentComposer composer;

  private final ILanguageFamilyGlobalScope symbolTable;

  private final Path outputPath;

  private Set<String> selectedWfrSetNames = new HashSet<>();

  private Set<String> unselectedWfrSetNames = new HashSet<>();


  public LanguageFamilyResolver(
      ILanguageComponentComposer composer,
      MCPath modelPath,
      Path outputPath) {
    this.symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");;
    this.outputPath = outputPath;
    this.parentFeatures = new ArrayDeque<>();
    this.featureNameToTmpLanguageComponent = new HashMap<>();
    this.composer = composer;
  }

  /**
   * Applies the language configuration and returns the resulting component
   *
   * @param pathTolanguageFamilyConfiguration the path to the configuration file
   * @return the composed language component after applying the passed configuration
   */
  public Optional<ASTLanguageComponentCompilationUnit> configure(
      String pathTolanguageFamilyConfiguration) {
    FeatureConfigurationTool featureConfigurationTool = new FeatureConfigurationTool();
    ASTFeatureConfiguration featureConfiguration = featureConfigurationTool.run(pathTolanguageFamilyConfiguration);


    if (featureConfiguration != null) {
      return configure(featureConfiguration);
    }
    return Optional.empty();
  }

  /**
   * Applies the language configuration and returns the resulting component
   *
   * @param config configuration of languagefamily as AST
   * @return the composed language component after applying the passed configuration
   */
  public Optional<ASTLanguageComponentCompilationUnit> configure(
      ASTFeatureConfiguration config) {

    ASTMCQualifiedName lfName = getLFFQN(config);

    Optional<LanguageFamilySymbol> lfSymbol = symbolTable
        .<LanguageFamilySymbol> resolveLanguageFamily(lfName.toString());
    if (lfSymbol.isPresent()) {
      return configureLanguageFamily(lfSymbol.get().getAstNode(),
          config);
    } else {
      Log.warn("Could not resolve language family symbol: " + lfName.toString());
    }
    return Optional.empty();
  }

  private ASTMCQualifiedName getLFFQN(ASTFeatureConfiguration featureConfiguration) {
    ASTFCCompilationUnit astfcCompilationUnit = (ASTFCCompilationUnit) featureConfiguration.getEnclosingScope().getAstNode();
    List<ASTMCImportStatement> familyQFN = astfcCompilationUnit.getMCImportStatementList();


    Optional<ASTMCImportStatement> importStatementWithFQNFamilyName = familyQFN.stream().filter(i -> i.getMCQualifiedName().containsParts(featureConfiguration.getFdName())).findFirst();

    if(importStatementWithFQNFamilyName.isEmpty()) {
      Log.error("Family is not imported in Feature Configuration");
    }

    return importStatementWithFQNFamilyName.get().getMCQualifiedName();
  }

  /**
   * Applies the given language family configuration to the family and returns
   * the resulting component.
   *
   * @param family the language family that should be configured
   * @param featureConfiguration the language configuration for the passed language family
   * @return the composed language component after applying the passed configuration
   */
  public Optional<ASTLanguageComponentCompilationUnit> configureLanguageFamily(
      ASTLanguageFamily family, ASTFeatureConfiguration featureConfiguration) {

    this.selectedFeatures = getSelectedFeatures(featureConfiguration);

    family.getFeaturesList().forEach(f -> featureDeclarations.put(f.getName(), f));
    // Composed Project should have the same name as configuration file
    String composedProjectName = featureConfiguration.getName();

    String rootFeatureName = family.getFeatureDiagram().getRootFeature();
    ASTFeatureDeclaration rootFeatureDeclaration = featureDeclarations.get(rootFeatureName);
    ASTMCQualifiedName rootLanguageComponentName = rootFeatureDeclaration.getRealizingComponentName();

    // Traverse feature tree and compose language components
    this.handle(family.getFeatureDiagram());

    String composedComponentPackage = Names.getPackageFromPath(
        Names.getPathFromQualifiedName(rootLanguageComponentName.toString()));

    // get the composed language component
    if (!this.getLanguageComponent(rootFeatureName).isPresent()) {
      return Optional.empty();
    }
    final ASTLanguageComponentCompilationUnit composedComponent = this.getLanguageComponent(rootFeatureName).get();

    final ASTLanguageComponentCompilationUnitBuilder builder = LanguageComponentBaseMill
        .languageComponentCompilationUnitBuilder();
    builder.setLanguageComponent(composedComponent.getLanguageComponent());
    final List<String> packageParts = Arrays.asList(composedComponentPackage.split("\\."));
    builder.setPackageList(packageParts);
    builder.addAllMCImportStatements(composedComponent.getMCImportStatementList());
    final ASTLanguageComponentCompilationUnit composedComponentCompUnit = builder.build();

    // output artifacts
    this.composer.getArtifactComposer().outputResult(composedProjectName);

    // output composed language component
    this.composer.getLanguageComponentProcessor()
        .printLanguageComponent(composedProjectName, composedComponentCompUnit, this.outputPath);

    // Building the new language family for the language product
    return Optional.of(composedComponentCompUnit);
  }

  private static Set<String> getSelectedFeatures(ASTFeatureConfiguration featureConfiguration) {
    return featureConfiguration.getFCElementList()
            .stream()
            .filter(e -> e instanceof ASTFeatures).map(f -> ((ASTFeatures) f).getNameList()).flatMap(List::stream).collect(Collectors.toSet());
  }

  /**
   * Returns the language component as modified by the visitor for the given
   * feature name.
   *
   * @param name Name of the feature to return the modified component for.
   * @return The modified component if present, wrapped in an Optional.
   */
  public Optional<ASTLanguageComponentCompilationUnit> getLanguageComponent(String name) {
    if (featureNameToTmpLanguageComponent.containsKey(name)) {
      return Optional.of(featureNameToTmpLanguageComponent.get(name));
    }
    return Optional.empty();
  }

  @Override
  public void visit(ASTFeatureDiagram node) {
    final ASTFeatureDeclaration astFeatureDeclaration = featureDeclarations
        .get(node.getRootFeature());
    final Optional<ASTLanguageComponentCompilationUnit> rootComponent = loadRealizingLanguageComponent(
        astFeatureDeclaration.getName());

    selectedWfrSetNames.addAll(
        astFeatureDeclaration.getRootConfigElementList().stream()
            .filter(e -> e instanceof ASTWfrSetRootConfig)
            .flatMap(e -> ((ASTWfrSetRootConfig) e).getNameList().stream())
            .collect(Collectors.toSet()));

    if (rootComponent.isPresent()) {
      unselectedWfrSetNames.addAll(rootComponent.get().getLanguageComponent().getWfrSetNames());
      unselectedWfrSetNames.removeIf(name -> selectedWfrSetNames.contains(name));
      featureNameToTmpLanguageComponent.put(
              node.getRootFeature(),
              rootComponent.get());
    }

    parentFeatures.push(node.getRootFeature());

  }

  @Override
  public void endVisit(ASTFeatureDiagram node) {
    final String rootFeature = node.getRootFeature();
    handleRootFeatureConfiguration(rootFeature);
  }

  @Override
  public void traverse(ASTFeatureTreeRule node) {
    // Skip traversing a subtree if the current feature is not selected
    // Optimized traversal time

    if (isSelected(node.getName())) {
      if (null != node.getFeatureGroup()) {
        node.getFeatureGroup().accept(this);
      }
    }
  }


  @Override
  public void visit(ASTGroupPart currentFeature) {
    Optional<ASTLanguageComponentCompilationUnit> realizingLanguageComponent = loadRealizingLanguageComponent(
            currentFeature.getName());
    if (realizingLanguageComponent.isPresent()) {
      featureNameToTmpLanguageComponent.put(
              currentFeature.getName(),
              realizingLanguageComponent.get());
    }

    parentFeatures.push(currentFeature.getName());
  }

  /**
   * Collect languageComponents of selected features and store them in map.
   */
  @Override
  public void endVisit(ASTGroupPart currentFeature) {

    parentFeatures.pop();
    if (!isSelected(currentFeature.getName())) {
      return;
    }

    final ASTFeatureDeclaration currentFeatureDeclaration = featureDeclarations
            .get(currentFeature.getName());

    if (!parentFeatures.isEmpty() && !currentFeatureDeclaration.isAbstractFeature()) {

      String parentFeature = nextParentNonAbstractFeature().get();

      ASTLanguageComponentCompilationUnit parentComponent = featureNameToTmpLanguageComponent
              .get(parentFeature);
      ASTLanguageComponentCompilationUnit realizingComponent = featureNameToTmpLanguageComponent
              .get(currentFeature.getName());

      List<ASTBindingElement> bindingElements = currentFeatureDeclaration.getBindingElementList();

      final List<Binding> bindings = convertBindings(bindingElements);

      List<String> shotgunSetNames = bindings.stream()
              .filter(b -> b.getExtensionPoint().isEmpty())
              .map(Binding::getProvisionPoint)
              .collect(Collectors.toList());
      ASTLanguageComponentCompilationUnit result = composer.composeLanguageComponent(
              realizingComponent,
              parentComponent,
              bindings);
      featureNameToTmpLanguageComponent.put(parentFeature, result);
      // Handling shotgun bindings means that we add a shotgun binding for the
      // set
      // to the parent component
      for (final String shotgunSetName : shotgunSetNames) {
        featureDeclarations.get(parentFeature)
                .addBindingElement(
                        LanguageFamilyMill.bindingElementBuilder()
                                .setWfrs(true)
                                .setProvidedElement(shotgunSetName)
                                .build());
      }
    }
  }

  private Optional<String> nextParentNonAbstractFeature() {
    for (Iterator<String> iterator = parentFeatures.iterator(); iterator.hasNext();) {
      String feature = iterator.next();
      if (featureDeclarations.get(feature).isPresentRealizingComponentName()) {
        return Optional.of(feature);
      }
    }
    return Optional.empty();
  }

  /**
   * Applies the root configuration and removes all unselected elements from the
   * root component
   *
   * @param rootFeature
   */
  private void handleRootFeatureConfiguration(String rootFeature) {
    ASTFeatureDeclaration rootFeatureDecl = featureDeclarations.get(rootFeature);
    ASTLanguageComponentCompilationUnit composedRootComponent = featureNameToTmpLanguageComponent
        .get(rootFeature);

    Set<ASTProvidedGenExtension> selectedGenPPs = new HashSet<>();
    Set<ASTProvidedGrammarExtension> selectedAsPPs = new HashSet<>();

    // Collect selected elements from root configuration
    for (ASTRootConfigElement rootConfig : rootFeatureDecl.getRootConfigElementList()) {
      if (rootConfig instanceof ASTRulePPRootConfig) {
        final List<String> nameList = ((ASTRulePPRootConfig) rootConfig).getNameList();
        for (final String name : nameList) {
          selectedAsPPs.add(composedRootComponent.getLanguageComponent().getGrammarProvisionPoint(name).get());
        }
      }
      if (rootConfig instanceof ASTGenPPRootConfig) {
        final List<String> nameList = ((ASTGenPPRootConfig) rootConfig).getNameList();
        for (final String name : nameList) {
          selectedGenPPs.add(composedRootComponent.getLanguageComponent().getGENProvisionPoint(name).get());
        }
      }
      if (rootConfig instanceof ASTWfrSetRootConfig) {
        composer.getArtifactComposer().addSelectedWfrSets(
            composedRootComponent.getLanguageComponent(),
            ((ASTWfrSetRootConfig) rootConfig).getNameList());
      }
    }

    if (composedRootComponent.getLanguageComponent().hasCompositionKeywords() &&
            !composedRootComponent.getLanguageComponent().hasAggregationExtensionPoints()){
    // Remove in root config unselected elements
    removeUnselectedGrammarPPsAndImpliedEPs(composedRootComponent.getLanguageComponent(), selectedAsPPs);
    removeUnselectedGeneratorPPsAndImpliedEPs(composedRootComponent.getLanguageComponent(), selectedGenPPs);
    removeUnselectedWfrSets(composedRootComponent.getLanguageComponent());
    }

    // Remove implications for removed elements
    Set<String> selectedASPPNames = selectedAsPPs.stream().map(p -> p.getName())
        .collect(Collectors.toSet());
    Set<String> selectedGENPPNames = selectedGenPPs.stream().map(p -> p.getName())
        .collect(Collectors.toSet());
    final Set<ASTImplication> implicationsToRemove = composedRootComponent.getLanguageComponent().getImplications()
        .stream()
        .filter(i -> !selectedASPPNames.contains(i.getSource())
            && !selectedGENPPNames.contains(i.getSource()))
        .collect(Collectors.toSet());
    composedRootComponent.getLanguageComponent().removeAllLanguageComponentElements(implicationsToRemove);
  }

  /**
   * Removes the wfr sets not selected in the root configuration.
   *
   * @param composedRootComponent
   */
  private void removeUnselectedWfrSets(ASTLanguageComponent composedRootComponent) {

    final Set<ASTWfrSetDefinition> unselectedSets = composedRootComponent.getWfrSetDefinitions()
        .stream()
        .filter(d -> unselectedWfrSetNames.contains(d.getName()))
        .collect(Collectors.toSet());
    composedRootComponent.removeAllLanguageComponentElements(unselectedSets);

    // Handle parameters that are not used in the selected wfr sets
    // The unused sets have already been removed and are not considered in
    // determining
    // the referenced rule names.
    final Set<String> referencedRuleNames = composedRootComponent.getWfrSetDefinitions()
        .stream()
        .flatMap(s -> s.getWfrDefinitionList().stream())
        .map(s -> s.getWfrReference().toString())
        .collect(Collectors.toSet());

    final Set<ASTParameter> unusedParameters = composedRootComponent.getParameters()
        .stream()
        .filter(ASTParameter::isWfr)
        .filter(p -> !referencedRuleNames.contains(p.getReference().toString()))
        .collect(Collectors.toSet());
    composedRootComponent.removeAllLanguageComponentElements(unusedParameters);
  }

  /**
   * Removes the unselected grammar pps from the component
   *
   * @param composedRootComponent
   * @param selectedAsPPs
   */
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
      removeImpliedEPsForPoint(composedRootComponent, selectedASImpliedEPs,
          unselectedAsPP.getName());
    }
  }

  /**
   * Removes the unselected generator pps from the component.
   *
   * @param composedRootComponent
   * @param selectedGenPPs
   */
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
      removeImpliedEPsForPoint(composedRootComponent, selectedGENImpliedEPs,
          unselectedGenPP.getName());
    }
  }

  /**
   * Removes all implied eps from the passed provision point from the component.
   *
   * @param composedRootComponent
   * @param selectedPointsImpliedEPs
   * @param unselectedPointName
   */
  private void removeImpliedEPsForPoint(
      ASTLanguageComponent composedRootComponent,
      Set<String> selectedPointsImpliedEPs,
      String unselectedPointName) {
    final Collection<String> unselectedGENImpliedEPs = composedRootComponent
        .getImpliedEPs(unselectedPointName);
    unselectedGENImpliedEPs.removeAll(selectedPointsImpliedEPs);

    for (final String impliedEP : unselectedGENImpliedEPs) {
      final Optional<ASTRequiredExtension> extensionPoint = composedRootComponent
          .getExtensionPoint(impliedEP);
      composedRootComponent.removeLanguageComponentElement(extensionPoint.get());
    }
  }



  private Optional<ASTLanguageComponentCompilationUnit> loadRealizingLanguageComponent(String featureName) {
    ASTFeatureDeclaration featureDeclaration = featureDeclarations.get(featureName);
    if (featureDeclaration == null) {
      /*
       * Log.error results in System.exit() and kills Maven build process.
       * TODO 1: disable quick Fail on SE Log globally for SCOLAR so we can log errors
       * TODO 2: refactor error handling as a whole in SCOLAR. Currently often empty Optionals are
       *     returned and no error is printed at all. Additionally, after logging a fatal error as here.
       *     The code should not continue with returning an empty optional...
       */
      Log.error(String.format("LF-Tool: Undefined feature: %s (missing feature declaration)", featureName));
      return Optional.empty();
    }
    
    if (featureDeclaration.isPresentRealizingComponentName()) {
      
      ASTMCQualifiedName realizingLanguageComponentName = featureDeclaration
          .getRealizingComponentName();
      
      // Load realizing LanguageComponent
      final Optional<LanguageComponentSymbol> realizingLanguageComponentSymbol = composer
          .getLanguageComponentProcessor()
          .loadLanguageComponentSymbol(realizingLanguageComponentName.getQName());
      
      if (!realizingLanguageComponentSymbol.isPresent()) {
        Log.warn(String.format("LF-Tool: LanguageComponent not found: %s (required by feature: %s)",
            realizingLanguageComponentName.toString(), featureName));
      }
      ASTLanguageFamilyCompilationUnitA astNode = (ASTLanguageFamilyCompilationUnitA) realizingLanguageComponentSymbol.get()
              .getEnclosingScope().getAstNode();

      return Optional.ofNullable(astNode.getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit());
    }
    
    return Optional.empty();
  }
  
  /**
   * Extract the bindings.
   *
   * @param bindings List of ast nodes of bindings.
   * @return List of bindings from a provided element name to the name of the
   * bound EP
   */
  private List<Binding> convertBindings(List<ASTBindingElement> bindings) {
    List<Binding> bindingNames = new ArrayList<>();
    for (ASTBindingElement bindingElement : bindings) {
      if (bindingElement.isProduction()) {
        bindingNames.add(new Binding(
            AS,
            bindingElement.getProvidedElement(),
            bindingElement.getEpName()));
      }
      if (bindingElement.isGen()) {
        bindingNames.add(new Binding(
            GEN,
            bindingElement.getProvidedElement(),
            bindingElement.getEpName()));
      }
      if (bindingElement.isWfrs()) {
        if (bindingElement.isPresentEpName()) {
          bindingNames.add(new Binding(
              WFR,
              bindingElement.getProvidedElement(),
              bindingElement.getEpName()));
        }
        else {
          bindingNames.add(new Binding(WFR, bindingElement.getProvidedElement(), ""));
        }
      }
    }
    return bindingNames;
  }
  
  private boolean isSelected(String featureName) {
    return selectedFeatures.contains(featureName);
  }

  protected Set<Object> traversedElements = new HashSet<>();

  @Override
  public Set<Object> getTraversedElements() {
    return traversedElements;
  }

  @Override
  public void setTraversedElements(Set<Object> traversedElements) {
    this.traversedElements = traversedElements;
  }
}
