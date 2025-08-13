/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package metacomposition;

import composition.AbstractArtifactComposer;
import composition.ILanguageComponentComposer;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.se_rwth.commons.Names;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.*;
import languagecomponentbase._symboltable.ILanguageComponentBaseGlobalScope;
import languagecomponentbase._symboltable.ILanguageComponentBaseScope;
import languagecomponentbase._symboltable.LanguageComponentBaseArtifactScope;
import util.Binding;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Language composer for the base language component language. Composes language
 * components according to bindings.
 *
 * @author Mutert
 * @author Pfeiffer
 */
public class BaseLanguageComponentComposer implements ILanguageComponentComposer {
  
  private static final String LOG_TAG = BaseLanguageComponentComposer.class.getSimpleName();
  
  protected AbstractArtifactComposer artifactComposer;

  protected LanguageComponentBaseProcessor languageComponentProcessor;

  /**
   * Constructor for metacomposition.BaseLanguageComponentComposer
   *
   * @param artifactComposer artifact composer
   * @param languageComponentProcessor language component processor
   */
  public BaseLanguageComponentComposer(
      AbstractArtifactComposer artifactComposer,
      LanguageComponentBaseProcessor languageComponentProcessor) {
    this.artifactComposer = artifactComposer;
    this.languageComponentProcessor = languageComponentProcessor;
  }

  /**
   * Constructor for metacomposition.BaseLanguageComponentComposer
   *
   * @param artifactComposer artifact composer
   * @param scope global scope
   */
  public BaseLanguageComponentComposer(
      AbstractArtifactComposer artifactComposer,
      ILanguageComponentBaseGlobalScope scope) {
    this.artifactComposer = artifactComposer;
    this.languageComponentProcessor = new LanguageComponentBaseProcessor(scope);
  }
  
  /**
   * @return languageComponentProcessor
   */
  public LanguageComponentBaseProcessor getLanguageComponentProcessor() {
    return this.languageComponentProcessor;
  }


  /**
   * Composes the language component ppComponentCompUnit with the epComponentCompUnit according
   * to the bindings given in the bindings parameter. To the extension points of
   * epComponentCompUnit the provided elements of ppComponentCompUnit are bound.
   *
   * @param ppComponentCompUnit ASTLanguageComponent of the languageComponent with the
   * provided points that are bound to the extension points
   * @param epComponentCompUnit ASTLanguageComponent that contains the extension points
   * which are bound
   * @param bindings The pairs of names from provided to extension point,
   * marking the binding of those
   * @return The composed {@link ASTLanguageComponent}
   */
  public ASTLanguageComponentCompilationUnit composeLanguageComponent(
          ASTLanguageComponentCompilationUnit ppComponentCompUnit,
          ASTLanguageComponentCompilationUnit epComponentCompUnit,
          List<Binding> bindings) {

    ASTLanguageComponentCompilationUnit epCompCompUnitAST = epComponentCompUnit.deepClone();
    final ASTLanguageComponentCompilationUnit ppCompCompUnitAST = ppComponentCompUnit.deepClone();

    epCompCompUnitAST.getEnclosingScope().getEnclosingScope().addSubScope(ppCompCompUnitAST.getEnclosingScope());

    if(epCompCompUnitAST.getEnclosingScope() instanceof  LanguageComponentBaseArtifactScope) {
      LanguageComponentBaseArtifactScope lcbs = (LanguageComponentBaseArtifactScope) epCompCompUnitAST.getEnclosingScope();
      for (ASTMCImportStatement astmcImportStatement : ppCompCompUnitAST.getMCImportStatementList()) {
        lcbs.addImports(new ImportStatement(astmcImportStatement.getQName(), astmcImportStatement.isStar()));
      }
    }

    if (bindings.isEmpty()) {
      return epCompCompUnitAST;
    }

    String composedCompName = epCompCompUnitAST.getLanguageComponent().getName() + "With" + ppCompCompUnitAST.getLanguageComponent().getName();
    epCompCompUnitAST.getLanguageComponent().setName(composedCompName);

    boolean genBindingFlag = false;
    // aggregation/embedding flag decide if method compose and/or aggregate is executed
    boolean aggregationFlag = false;
    boolean embeddingFlag = false;

    // composes language components
    for (Binding binding : bindings) {
      boolean isAggregate = false;
      Optional<ASTRequiredExtension> extPointDefinition = epCompCompUnitAST.getLanguageComponent()
              .getExtensionPoint(binding.getExtensionPoint());

      // checks required extension point if it is an embedding or an aggregation
      if (extPointDefinition.isPresent() && extPointDefinition.get().isPresentComposition()) {
        if (extPointDefinition.get().isAggregate()) {
          isAggregate = true;
          aggregationFlag = true;
        } else {
          embeddingFlag = true;
        }
      } else if (extPointDefinition.isPresent()) {
        embeddingFlag = true;
      }

      switch (binding.getBindingType()) {
        case WFR:
          applyWFRBinding(binding, ppCompCompUnitAST, epCompCompUnitAST);
          break;
        case GEN:
          if (isAggregate) {
            applyGENBindingForAggregation(binding, ppCompCompUnitAST, epCompCompUnitAST);
            genBindingFlag = true;
          } else {
            applyGENBinding(binding, ppCompCompUnitAST, epCompCompUnitAST);
            genBindingFlag = true;
          }
          break;
        case AS:
          if (isAggregate) {
            applyASBindingForAggregation(binding, ppCompCompUnitAST, epCompCompUnitAST);
          } else {
            applyASBinding(binding, ppCompCompUnitAST, epCompCompUnitAST);
          }
          break;
      }
    }

    if (aggregationFlag) {
      // run the artifact composition for aggregation
      this.getArtifactComposer().aggregate(ppComponentCompUnit, epComponentCompUnit, bindings);
    }
    if (embeddingFlag) {
      // run the artifact composition for embedding
      this.getArtifactComposer().compose(ppComponentCompUnit, epComponentCompUnit, bindings);

      ASTMCQualifiedName composedASName = getQualifiedName(
              getArtifactComposer().getComposedGrammarName());
      epCompCompUnitAST.getLanguageComponent().getGrammarDefinition().setMCQualifiedName(composedASName);
    }

    // ======================== Parameters ========================
    // 1. Only add the parameters for CoCos which are present in at least one of
    // the coco sets in the composed language component
    // 2. Only add the generator parameters for generators that are present in
    // the composed component
    
    for (ASTParameter ppCompParam : ppCompCompUnitAST.getLanguageComponent().getParameters()) {
      if (ppCompParam.isWfr()) {
        addWFRParam(ppCompParam, epCompCompUnitAST.getLanguageComponent());
      }
      else {
        addTrafoParam(ppCompParam, epCompCompUnitAST.getLanguageComponent());
      }
    }

    // add the domain models of adapted generator pps to the composed component
    if (genBindingFlag) {
      addDomainModels(ppCompCompUnitAST, epCompCompUnitAST);
    }

    return epCompCompUnitAST;
  }
  
  protected ASTMCQualifiedName getQualifiedName(String name) {
    ASTMCQualifiedNameBuilder qualifiedNameBuilder = MCBasicTypesMill.mCQualifiedNameBuilder();
    if (name.contains(".")) {
      for (String part : name.split("\\.")) {
        qualifiedNameBuilder.addParts(part);
      }
    }
    else {
      qualifiedNameBuilder.addParts(name);
    }
    
    return qualifiedNameBuilder.build();
    
  }
  
  /**
   * Adds all domain models referenced by adapted generators of the ppComp to
   * the composedComp.
   * 
   * @param ppComp providing component
   * @param composedComp composed component
   */
  protected void addDomainModels(ASTLanguageComponentCompilationUnit ppComp, ASTLanguageComponentCompilationUnit composedComp) {
    composedComp.addAllMCImportStatements(ppComp.getMCImportStatementList());
  }
  
  protected void applyGENBinding(Binding binding, ASTLanguageComponentCompilationUnit ppComp,
                                 ASTLanguageComponentCompilationUnit epComp) {
    applyASBinding(binding, ppComp, epComp);
  }

  protected void applyGENBindingForAggregation(Binding binding, ASTLanguageComponentCompilationUnit ppComp,
                                 ASTLanguageComponentCompilationUnit epComp) {
    applyASBindingForAggregation(binding, ppComp, epComp);
  }

  /**
   * Method for embedding the grammar. Applies the binding of grammar pps to eps. This includes adding the implied
   * eps of the embedded pp and changing the optionality of the bound ep.
   * 
   * @param binding bindings
   * @param ppComp providing component
   * @param epComp requiring component
   */
  protected void applyASBinding(Binding binding, ASTLanguageComponentCompilationUnit ppComp,
                                ASTLanguageComponentCompilationUnit epComp) {
    
    addImpliedEPs(epComp.getLanguageComponent(), ppComp.getLanguageComponent(), binding);
    
    addImplications(epComp.getLanguageComponent(), ppComp.getLanguageComponent(), binding);
    
    Optional<ASTRequiredExtension> extPointDefinition = epComp.getLanguageComponent()
        .getExtensionPoint(binding.getExtensionPoint());
    
    // Adjust the optionality/obligation of the extension point
    extPointDefinition.ifPresent(this::adjustOptionality);
    
  }
  /**
   * Method for aggregating the grammar. Applies the binding of grammar pps (provided extension) to eps (required extension).
   * This includes changing the optionality of the bound ep and adding all grammar and generator extensions to the component with the ep.
   * The cardinality of referenced grammars are no longer limited to only one referenced grammar.
   *
   * @param binding bindings
   * @param ppComp providing component
   * @param epComp requiring component
   */
  protected void applyASBindingForAggregation(Binding binding, ASTLanguageComponentCompilationUnit ppComp,
                                              ASTLanguageComponentCompilationUnit epComp) {

    // add grammar/gen/wfrs extensions to required extensions
    addGrammarExtensions(ppComp.getLanguageComponent(), epComp.getLanguageComponent());
    addGenExtensions(ppComp.getLanguageComponent(), epComp.getLanguageComponent());
    addWfrs(ppComp.getLanguageComponent(), epComp.getLanguageComponent());

    // add all grammars of aggregated component B to aggregating component A
    addGrammars(ppComp.getLanguageComponent(), epComp.getLanguageComponent());

    Optional<ASTRequiredExtension> extPointDefinition = epComp.getLanguageComponent()
            .getExtensionPoint(binding.getExtensionPoint());

    // Adjust the optionality/obligation of the extension point
    extPointDefinition.ifPresent(this::adjustOptionality);
  }

  /**
   * Method for adding grammar extensions of aggregated component to grammar extensions of aggregating component.
   * Adds all productions from ppComp to epComp.
   *
   * @param providingComp providing component
   * @param composedComp requiring component that gets all grammars extensions
   */
  protected void addGrammarExtensions(ASTLanguageComponent providingComp, ASTLanguageComponent composedComp) {

    final Collection<ASTRequiredGrammarExtension> requiredGrammarExtensions = providingComp.getGrammarExtensionPoints();
    final Collection<ASTProvidedGrammarExtension> providedGrammarExtensions = providingComp.getGrammarProvisionPoints();

    if (!requiredGrammarExtensions.isEmpty()) {
      for (ASTRequiredGrammarExtension extension : requiredGrammarExtensions) {
        if (!composedComp.containsLanguageComponentElement(extension)) {
          composedComp.addLanguageComponentElement(extension);
        }
      }
    }

    if (!providedGrammarExtensions.isEmpty()) {
      for (ASTProvidedGrammarExtension extension : providedGrammarExtensions) {
        if (!composedComp.containsLanguageComponentElement(extension)) {
          composedComp.addLanguageComponentElement(extension);
        }
      }
    }
  }

  /**
   * Method for adding generator extensions of aggregated component to generator extensions of aggregating component.
   * Adds all generators from ppComp to epComp.
   *
   * @param providingComp providing component
   * @param composedComp requiring component that gets all generator extensions
   */
  protected void addGenExtensions(ASTLanguageComponent providingComp, ASTLanguageComponent composedComp) { // needs testing

    final Collection<ASTRequiredGenExtension> requiredGenExtensions = providingComp.getGENExtensionPoints();
    final Collection<ASTProvidedGenExtension> providedGenExtensions = providingComp.getGENProvisionPoints();

    if (!requiredGenExtensions.isEmpty()) {
      for (ASTRequiredGenExtension extension : requiredGenExtensions) {
        if (!composedComp.containsLanguageComponentElement(extension)) {
          composedComp.addLanguageComponentElement(extension);
        }
      }
    }

    if (!providedGenExtensions.isEmpty()) {
      for (ASTProvidedGenExtension extension : providedGenExtensions) {
        if (!composedComp.containsLanguageComponentElement(extension)) {
          composedComp.addLanguageComponentElement(extension);
        }
      }
    }
  }

  /**
   * Method for adding wfrs of aggregated component to aggregating component.
   * Adds all generators from ppComp to epComp.
   *
   * @param providingComp providing component
   * @param composedComp requiring component that gets all wfrs
   */
  protected void addWfrs(ASTLanguageComponent providingComp, ASTLanguageComponent composedComp) {
    final Collection<ASTWfrSetDefinition> wfrsExtensions = providingComp.getWfrSetDefinitions();

    if (!wfrsExtensions.isEmpty()) {
      for (ASTWfrSetDefinition extension : wfrsExtensions) {
        if (!composedComp.containsLanguageComponentElement(extension)) {
          composedComp.addLanguageComponentElement(extension);
        }
      }
    }
  }

  /**
   * Method for adding grammars of aggregated component B to aggregating component A.
   * Adds all from providingComp to composedComp.
   *
   * @param providingComp providing component
   * @param composedComp requiring component that gets all grammars
   */
  protected void addGrammars(ASTLanguageComponent providingComp, ASTLanguageComponent composedComp) {
    final List<ASTGrammarDefinition> providingGrammars = providingComp.getGrammarDefinitionList();

    for (ASTGrammarDefinition grammar : providingGrammars) {
      if (!composedComp.containsGrammarDefinition(grammar)) {
        composedComp.addGrammarDefinition(grammar);
      }
    }
  }

  /**
   * Add implications from each provides point implying the EP to each implied
   * EP of the bound PP
   *
   * @param composedComp composed component the implied eps are added to
   * @param providingComp providing component
   * @param binding bindings
   */
  protected void addImplications(ASTLanguageComponent composedComp,
      ASTLanguageComponent providingComp, Binding binding) {
    final String providedPointName = binding.getProvisionPoint();
    
    final Collection<String> impliedEPs = providingComp.getImpliedEPs(providedPointName);
    
    if (!impliedEPs.isEmpty()) {
      final Collection<String> implyingPps = composedComp
          .getImplyingPps(binding.getExtensionPoint());
      
      for (String implyingPp : implyingPps) {
        composedComp.addLanguageComponentElement(
            LanguageComponentBaseMill.implicationBuilder()
                .setSource(implyingPp)
                .addAllTarget(impliedEPs)
                .build());
      }
    }
  }
  
  /**
   * Add all implied extension points to the list of extension points of the
   * embedding component. Filters duplicate extension points which might have
   * been added previously.
   * 
   * @param composedComp composed component
   * @param ppComp providing component
   * @param binding bindings
   */
  protected void addImpliedEPs(ASTLanguageComponent composedComp, ASTLanguageComponent ppComp,
      Binding binding) {
    final String providedPointName = binding.getProvisionPoint();
    
    final Collection<String> impliedEPs = ppComp.getImpliedEPs(providedPointName);
    
    composedComp.addAllLanguageComponentElements(
        ppComp.getExtensionPoints()
            .stream()
            .filter(p -> impliedEPs.contains(p.getName()))
            .filter(p -> !composedComp.getLanguageComponentElementList().contains(p))
            .collect(Collectors.toList()));
  }
  
  /**
   * Applies wfr binding. Includes merging or adding wfr sets.
   * 
   * @param binding wfr binding
   * @param ppComp providing component
   * @param epComp requiring component
   */
  protected void applyWFRBinding(Binding binding, ASTLanguageComponentCompilationUnit ppComp,
                                 ASTLanguageComponentCompilationUnit epComp) {
    final Optional<ASTWfrSetDefinition> wfrDefinition = ppComp
        .getLanguageComponent().getWfrSetDefinition(binding.getProvisionPoint());
    
    // ========= Error checking =========
    if (!wfrDefinition.isPresent()) {
      return;
    }
    
    // Is WFR binding? Yes --> The sets should be merged
    if (!binding.getExtensionPoint().isEmpty()) {
      final Optional<ASTWfrSetDefinition> epCompWFRSet = epComp.getLanguageComponent()
          .getWfrSetDefinition(binding.getExtensionPoint());
      if (!epCompWFRSet.isPresent()) {
        return;
      }
      Set<ASTWfrDefinition> epCompWFRSetCoCos = new HashSet<>(
          epCompWFRSet.get().getWfrDefinitionList());
      
      final Set<ASTWfrDefinition> ppCompNewCoCos = wfrDefinition.get()
          .getWfrDefinitionList()
          .stream()
          .filter(e -> !epCompWFRSetCoCos.contains(e))
          .collect(Collectors.toSet());
      epCompWFRSet.get().getWfrDefinitionList().addAll(ppCompNewCoCos);
    }
    
    // The set from the ppComponent should be added as a separate WFR set
    else {
      epComp.getLanguageComponent().getLanguageComponentElementList().add(wfrDefinition.get());
    }
  }
  
  /**
   * Adds generator parameters of the providing component to the embedding
   * component for all available generators.
   * 
   * @param ppCompParam providing component
   * @param comp composed component
   */
  protected void addTrafoParam(ASTParameter ppCompParam, ASTLanguageComponent comp) {
    // stores all producer and product interfaces of the component
    List<String> interfaces = comp.getGENExtensionPoints().stream()
        .map(ep -> ep.getProducerInterfaceRef(0).getName().toString())
        .collect(Collectors.toList());
    interfaces.addAll(comp.getGENExtensionPoints().stream()
        .map(ep -> ep.getProductInterfaceRef(0).getName().toString())
        .collect(Collectors.toList()));
    
    if (interfaces.contains(ppCompParam.getReference().toString())) {
      comp.addLanguageComponentElement(ppCompParam);
    }
  }
  
  /**
   * Adds wfr parameters from the providing component to the composed component
   * for wfrs that are available in the composed component.
   * 
   * @param ppCompParam providing component
   * @param comp composed component
   */
  protected void addWFRParam(ASTParameter ppCompParam, ASTLanguageComponent comp) {
    final Collection<ASTWfrDefinition> allContextConditions = comp.getAllContextConditions();
    final Set<String> referencedCoCoNames = allContextConditions.stream()
        .map(c -> c.getWfrReference().toString())
        .collect(Collectors.toSet());
    ASTMCQualifiedName cocoReference = ppCompParam.getReference();
    
    if (referencedCoCoNames.contains(cocoReference.toString())) {
      if (!comp.getParameterNames().contains(ppCompParam.getName())) {
        comp.addLanguageComponentElement(ppCompParam);
      }
    }
  }

  /**
   * Sets the optionality of the extension point to optional
   *
   * @param extPointDefinition The extension point to modify
   */
  protected void adjustOptionality(ASTRequiredExtension extPointDefinition) {
    extPointDefinition.setOptionality(ASTOptionality.OPTIONAL);
  }

  @Override
  public AbstractArtifactComposer getArtifactComposer() {
    return this.artifactComposer;
  }

}
