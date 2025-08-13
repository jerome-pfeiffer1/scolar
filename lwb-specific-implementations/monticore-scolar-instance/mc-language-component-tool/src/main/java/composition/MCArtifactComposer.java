package composition;

import static util.Binding.BindingType.AS;
import static util.Binding.BindingType.GEN;
import static util.Binding.BindingType.WFR;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTParameter;
import languagecomponentbase._ast.ASTRequiredExtension;
import projectgeneration.ProjectGenerator;
import util.Binding;

/**
 * Composes MC grammars, CoCos and generators in the technological space of
 * MontiCore.
 *
 * @author Mutert
 * @author Pfeiffer
 */


public class MCArtifactComposer extends AbstractArtifactComposer {

  private final MCGrammarArtifactComposerHelper grammarComposerHelper;
  
  private final MCGeneratorArtifactComposerHelper generatorComposer;
  
  private final MCWfrArtifactComposerHelper wfrComposer;

  private final ProjectGenerator projectGenerator;
  
  private Optional<String> lastComposedComponentName = Optional.empty();
  
  private String lastGrammarPackageName = "";
  
  private String lastComposedGrammarName = "";

  private HashSet<String> grammarPackages = new HashSet<>();

/**
   * Constructor for composition.AbstractArtifactComposer
   *
   * @param modelPath
   * @param outputPath
   */

  public MCArtifactComposer(MCPath modelPath, Path outputPath) throws IOException {
    super(modelPath, outputPath);
    this.grammarComposerHelper = new MCGrammarArtifactComposerHelper(modelPath);
    this.generatorComposer = new MCGeneratorArtifactComposerHelper(modelPath);
    this.wfrComposer = new MCWfrArtifactComposerHelper();
    this.projectGenerator = new ProjectGenerator(modelPath, outputPath);
  }
  
  @Override
  public void compose(
      ASTLanguageComponentCompilationUnit ppComponent,
      ASTLanguageComponentCompilationUnit epComponent,
      Collection<Binding> bindings) {
    
    String composedComponentName = epComponent.getLanguageComponent().getName() + "With" + ppComponent.getLanguageComponent().getName();
    lastComposedComponentName = Optional.of(composedComponentName);
    
    if (epComponent.getLanguageComponent().getReferencedGrammarName().equals(ppComponent.getLanguageComponent().getReferencedGrammarName())) {
      lastComposedGrammarName = epComponent.getLanguageComponent().getASReference();
    }
    else {
      lastComposedGrammarName = epComponent.getLanguageComponent().getASReference()
          + "With" + ppComponent.getLanguageComponent().getReferencedGrammarName();
    }
    lastGrammarPackageName = epComponent.getLanguageComponent().getReferencedGrammarPackage();

    // Grammar
    if (!filterBindings(bindings, AS).isEmpty()) {
      this.grammarComposerHelper.compose(
          ppComponent.getLanguageComponent(), epComponent.getLanguageComponent(), bindings,
          composedComponentName, this.lastComposedGrammarName);
    }
    
    // CoCos
    this.wfrComposer.composeWFR(
        ppComponent.getLanguageComponent(),
        epComponent.getLanguageComponent(),
        filterBindings(bindings, WFR).stream()
            .filter(e -> !e.getExtensionPoint().isEmpty())
            .collect(Collectors.toList()),
        composedComponentName,
        lastComposedGrammarName);
    this.wfrComposer.addCoCos(
        ppComponent.getLanguageComponent(),
        filterBindings(bindings, WFR).stream()
            .filter(e -> e.getExtensionPoint().isEmpty())
            .map(Binding::getProvisionPoint)
            .collect(Collectors.toList()));
    
    // Generator
    genComposition(ppComponent, epComponent, bindings);
  }

  @Override
  public void aggregate(
          ASTLanguageComponentCompilationUnit ppComponent,
          ASTLanguageComponentCompilationUnit epComponent,
          Collection<Binding> bindings) {

    String composedComponentName = epComponent.getLanguageComponent().getName() + "With" + ppComponent.getLanguageComponent().getName();
    lastComposedComponentName = Optional.of(composedComponentName);

    String ppPackageName = Names.getQualifiedName(ppComponent.getPackageList());
    String epPackageName = Names.getQualifiedName(epComponent.getPackageList());

    // Grammar
    for (Binding b : filterBindings(bindings, AS)) {
      Optional<ASTRequiredExtension> extPointDefinition = epComponent.getLanguageComponent()
              .getExtensionPoint(b.getExtensionPoint());

      if (extPointDefinition.isPresent() && extPointDefinition.get().isPresentComposition() && extPointDefinition.get().isAggregate()) {
          try {
              this.grammarComposerHelper.aggregate(b, ppComponent.getLanguageComponent(),
                      epComponent.getLanguageComponent(), ppPackageName, epPackageName, outputPath);
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
      }
    }

    // CoCos // Bindings between WFRs have no composition keyword. This will never be called without composition keyword.
    if (!filterBindings(bindings, WFR).isEmpty()) {
      this.wfrComposer.composeWFR(
              ppComponent.getLanguageComponent(), epComponent.getLanguageComponent(), bindings,
              composedComponentName, this.lastComposedGrammarName);
    }

    // Generator // Aggregation Binding between GENs is the same as in embedding.
    genComposition(ppComponent, epComponent, bindings);
  }

  public void genComposition(ASTLanguageComponentCompilationUnit ppComponent,
                             ASTLanguageComponentCompilationUnit epComponent,
                             Collection<Binding> bindings) {

    for (Binding b : filterBindings(bindings, GEN)) {

      // collect all binding information for the generator adapter generation
      this.generatorComposer.collectGeneratorAdapters(lastGrammarPackageName, b, ppComponent, epComponent, outputPath);

      this.generatorComposer.compose(this.lastComposedComponentName.get(), lastGrammarPackageName,
              lastComposedGrammarName, b, ppComponent, epComponent);

      // add package to a list for the project generator to generate packages in directory
      grammarPackages.add(ppComponent.getPackage(0));
      grammarPackages.add(epComponent.getPackage(0));
    }
  }
  
/**
   * Filters all given bindings to the given bindingType
   *
   * @param bindings List of bindings to filter from
   * @param bindingType Type of bindings to filter
   * @return Collection of bindings containing only the filtered type
   */
  private Collection<Binding> filterBindings(
      Collection<Binding> bindings,
      Binding.BindingType bindingType) {
    
    return bindings.stream()
        .filter(b -> b.getBindingType().equals(bindingType))
        .collect(Collectors.toSet());
  }
  
  @Override
  public void setParameter(ASTLanguageComponent lc, ASTParameter param, String value) {
    if (param.isWfr()) {
      this.wfrComposer.setParameter(lc, param, value);
    }
    else if (param.isTransformation()) {
      generatorComposer.setParameter(lc, param, value);
    }
  }
  
  @Override
  public void outputResult(String composedProjectName) {
    projectGenerator.outputResult(outputPath, Optional.ofNullable(composedProjectName), grammarPackages, lastGrammarPackageName);

      try {
          grammarComposerHelper.outputResult(outputPath, composedProjectName, lastGrammarPackageName);
      } catch (IOException e) {
          throw new RuntimeException(e);
      }

      try {
      this.generatorComposer.outputResult(outputPath, lastComposedGrammarName,
          lastGrammarPackageName, composedProjectName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (lastComposedComponentName.isPresent()) {
      this.wfrComposer.outputResult(
          composedProjectName,
          this.outputPath,
          this.lastComposedComponentName.get(),
          this.lastGrammarPackageName,
          this.lastComposedGrammarName);
    }

      // Reset fields
    this.lastComposedComponentName = Optional.empty();
    this.lastGrammarPackageName = "";
    this.lastComposedGrammarName = "";
  }
  
  @Override
  public void addSelectedWfrSets(ASTLanguageComponent rootComponent, List<String> wfrSetNames) {
    // Adds the names to a set of allowed wfr sets that are to be printed along
    // side the shotgun coco sets
    wfrSetNames.forEach(wfrComposer::addStartSet);
  }

    @Override
  public String getComposedGrammarName() {
    return lastComposedGrammarName;
  }
  
  @Override
  public String getGeneratorName() {
    return lastComposedGrammarName + "Gen";
  }
  
  @Override
  public String getGeneratorDomainModelName() {
    return generatorComposer.getDomainModelName();
  }
  
}
