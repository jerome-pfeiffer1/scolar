/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package composition;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import de.monticore.io.paths.MCPath;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTParameter;
import util.Binding;

/**
 * Abstract base class for artifact composers that is extended for the artifact
 * composition of workbenches.
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public abstract class AbstractArtifactComposer {
  
  /**
   * Path where all referenced artifacts reside
   */
  protected MCPath modelPath;
  
  /**
   * Output path for the composed artifacts
   */
  protected Path outputPath;


  /**
   * @param modelPath path of source models
   * @param outputPath out put path for composed artifacts
   */
  public AbstractArtifactComposer(MCPath modelPath, Path outputPath) {
    this.modelPath = modelPath;
    this.outputPath = outputPath;
  }

  /**
   * Returns the name of the composed grammar. Can be used by the component composer.
   *
   * @return the name of the composed grammar
   */
  public abstract String getComposedGrammarName();
  
  /**
   * Returns the name of the composed generator. Can be used by the component composer. 
   * @return the name of the composed generator
   */
  public abstract String getGeneratorName();
  
  /**
   * returns the domain model of the composed generator. Can be used by the component composer.
   * @return the name of the domain model of the composed generator
   */
  public abstract String getGeneratorDomainModelName();
  
  /**
   * Selects the coco sets for the composed language
   * 
   * @param rootComponent The component for which to select the wfr sets
   * @param wfrSetNames The names of the selected wfr sets
   */
  public abstract void addSelectedWfrSets(
      ASTLanguageComponent rootComponent,
      List<String> wfrSetNames);
  
  /**
   * Composes (embeds) the artifacts of the ppComponent component with the artifacts of
   * the epComponent.
   *
   * @param ppComponent the component of which the artifacts need to be composed
   * into epComponents artifacts
   * @param epComponent the component ppComponent's artifacts are to be
   * integrated in.
   * @param bindings of provision points to extension points
   */
  public abstract void compose(
          ASTLanguageComponentCompilationUnit ppComponent,
          ASTLanguageComponentCompilationUnit epComponent,
          Collection<Binding> bindings);

  /**
   * Aggregates the artifacts of the ppComponent component with the artifacts of
   * the epComponent.
   *
   * @param ppComponent the component of which the artifacts need to be composed
   * into epComponents artifacts
   * @param epComponent the component ppComponent's artifacts are to be
   * integrated in.
   * @param bindings of provision points to extension points
   */
  public abstract void aggregate(
          ASTLanguageComponentCompilationUnit ppComponent,
          ASTLanguageComponentCompilationUnit epComponent,
          Collection<Binding> bindings);

  /**
   * Sets the parameter for a wfr or transformation.
   * 
   * @param lc the language component where to set the
   * @param param parameter
   * @param value and its value
   */
  public abstract void setParameter(
      ASTLanguageComponent lc,
      ASTParameter param,
      String value);
  
  /**
   * Outputs the result of the artifact composition. Usually this generates a
   * new file with the composed content to the output path
   */
  public abstract void outputResult(String composedProjectName);
  
  /**
   * Getter for the model path.
   * 
   * @return The model path of the composer.
   */
  public MCPath getModelPath() {
    return this.modelPath;
  }
  
  /**
   * @return outputPath of the composer
   */
  public Path getOutputPath() {
    return outputPath;
  }
}
