/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package composition;

import java.util.List;

import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import util.Binding;

/**
 * Interface for composers of language components. Enables to adapt the
 * composition to new language component constituents.
 *
 * @author Mutert
 * @author Pfeiffer
 */
public interface ILanguageComponentComposer {
  
  /**
   * Returns the processor for the kind of language components that the composer
   * is adapted to.
   * 
   * @return language component processor
   */
  LanguageComponentBaseProcessor getLanguageComponentProcessor();
  
  /**
   * Returns an artifact composer for the referenced language artifacts.
   * 
   * @return artifact composer
   */
  AbstractArtifactComposer getArtifactComposer();
  
  /**
   * Composes the language component ppComponent with the epComponent according
   * to the bindings given in the bindings parameter. To the extension points of
   * epComponent the provided elements of ppComponent are bound.
   *
   * @param ppComponent ASTLanguageComponent of the languageComponent with the
   * provided points that are bound to the extension points
   * @param epComponent ASTLanguageComponent that contains the extension points
   * which are bound
   * @param bindings The pairs of names from provided to extension point,
   * marking the binding of those
   * @return The composed {@link ASTLanguageComponentCompilationUnit}
   */
  ASTLanguageComponentCompilationUnit composeLanguageComponent(
          ASTLanguageComponentCompilationUnit ppComponent,
          ASTLanguageComponentCompilationUnit epComponent,
          List<Binding> bindings);
}
