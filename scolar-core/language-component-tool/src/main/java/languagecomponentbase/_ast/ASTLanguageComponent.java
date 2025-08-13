package languagecomponentbase._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Names;

import java.util.*;
import java.util.stream.Collectors;

public class ASTLanguageComponent extends ASTLanguageComponentTOP {

  public ASTLanguageComponent() {
    super();
  }

  public ASTLanguageComponent(String name, ASTGrammarDefinition grammarDefinition,
      List<ASTLanguageComponentElement> languageComponentElements) {
    super();
    setName(name);
    setGrammarDefinition(0, grammarDefinition);
    setLanguageComponentElementList(languageComponentElements);
  }

  public ASTLanguageComponent(String name, List<ASTGrammarDefinition> grammarDefinitions, List<ASTLanguageComponentElement> languageComponentElements) {
    super();
    setName(name);
    setGrammarDefinitionList(grammarDefinitions);
    setLanguageComponentElementList(languageComponentElements);
  }
  
  /**
   * Returns whether the language component is complete.<br>
   * <br>
   * A language component is complete when no mandatory (generator and grammar) extension points and
   * undefined parameters are left.
   * 
   * @return Whether the language component is complete
   */
  public boolean isComplete() {
    return !this.hasMandatoryExtensionPoints() && !this.hasMandatoryParameters();
  }
  
  public boolean hasMandatoryParameters() {
    return this.getParameters()
        .stream()
        .anyMatch(ASTParameter::isMandatory);
  }
  
  public boolean hasMandatoryExtensionPoints() {
    return this.getExtensionPoints()
        .stream()
        .anyMatch(ASTRequiredExtension::isMandatory);
  }

  public boolean hasAggregationExtensionPoints() {
    return this.getExtensionPoints()
            .stream()
            .anyMatch(ASTRequiredExtension::isAggregate);
  }

  public boolean isAggregationExtensionPoint(String extension) {
    return this.getExtensionPoint(extension).stream().anyMatch(ASTRequiredExtension::isAggregate);
  }

  public boolean isEmbeddingExtensionPoint(String extension) {
    return this.getExtensionPoint(extension).stream().anyMatch(ASTRequiredExtension::isEmbed);
  }

  public boolean hasEmbeddingExtensionPoints() {
    return this.getExtensionPoints()
            .stream()
            .anyMatch(ASTRequiredExtension::isEmbed);
  }

  public boolean hasCompositionKeywords() {
    return this.getExtensionPoints()
            .stream()
            .anyMatch(ASTRequiredExtension::isPresentComposition);
  }
  
  /**
   * @return The parameters defined in the languageComponent
   */
  public Set<ASTParameter> getParameters() {
    return this.getLanguageComponentElementList()
        .stream()
        .filter(e -> e instanceof ASTParameter)
        .map(e -> (ASTParameter) e)
        .collect(Collectors.toSet());
  }

  public Set<String> getParameterNames() {
    return this.getParameters().stream()
        .map(ASTParameter::getName)
        .collect(Collectors.toSet());
  }

  /**
   * Determine all domain model definitions in the language component.
   * @return The list of all domain model definitions.
   */
  public List<ASTDomainModelDefinition> getASTDomainModelDefinitionList() {
    return
        this.languageComponentElements
            .stream()
            .filter(e -> e instanceof ASTDomainModelDefinition)
            .map(e -> (ASTDomainModelDefinition) e)
            .collect(Collectors.toList());
  }

  /**
   * Returns the referenced grammar name as a String.
   * @return The referenced grammar name as a String.
   */
  public String getASReference() {
    return this.getGrammarDefinition().getMCQualifiedName().toString();
  }

  /**
   * Returns a list of names which are referenced by the Langauge Component.
   * @return The referenced grammar names as a list of Strings.
   */
  public Collection<String> getASReferences() {
    return this.getGrammarDefinitionList()
            .stream()
            .map(e -> e.getMCQualifiedName().toString())
            .collect(Collectors.toList());
  }

  public Collection<ASTRequiredExtension> getExtensionPoints() {
    return this.getLanguageComponentElementList()
        .stream()
        .filter(e -> e instanceof ASTRequiredExtension)
        .map(e -> (ASTRequiredExtension) e)
        .collect(Collectors.toList());
  }

  public Set<String> getExtensionPointNames() {
    return this.getExtensionPoints().stream()
        .map(ASTRequiredExtension::getName)
        .collect(Collectors.toSet());
  }

  public Optional<ASTRequiredExtension> getExtensionPoint(String name) {
    return getExtensionPoints().stream().filter(e -> e.getName().equals(name)).findFirst();
  }

  public Collection<ASTProvidedExtension> getProvisionPoints() {
    return this.getLanguageComponentElementList()
        .stream()
        .filter(e -> e instanceof ASTProvidedExtension)
        .map(e -> (ASTProvidedExtension) e)
        .collect(Collectors.toList());
  }

  public Optional<ASTProvidedExtension> getProvisionPoint(String name) {
    return getProvisionPoints().stream().filter(e -> e.getName().equals(name)).findFirst();
  }

  public Collection<ASTProvidedGrammarExtension> getGrammarProvisionPoints() {
    return getProvisionPoints().stream()
        .filter(e -> e instanceof ASTProvidedGrammarExtension)
        .map(e -> (ASTProvidedGrammarExtension) e)
        .collect(Collectors.toList());
  }

  public Collection<ASTRequiredGrammarExtension> getGrammarExtensionPoints() {
    return getExtensionPoints().stream()
        .filter(e -> e instanceof ASTRequiredGrammarExtension)
        .map(e -> (ASTRequiredGrammarExtension) e)
        .collect(Collectors.toList());
  }

  public Optional<ASTRequiredGrammarExtension> getGrammarExtensionPoint(String name) {
    return getGrammarExtensionPoints()
               .stream()
               .filter(e -> e.getName().equals(name))
               .findFirst();
  }

  public Optional<ASTProvidedGrammarExtension> getGrammarProvisionPoint(String name) {
    return getGrammarProvisionPoints().stream().filter(e -> e.getName().equals(name)).findFirst();
  }

  public Collection<ASTWfrSetDefinition> getWfrSetDefinitions() {
    return this.getLanguageComponentElementList().stream()
        .filter(e -> e instanceof ASTWfrSetDefinition)
        .map(e -> (ASTWfrSetDefinition) e)
        .collect(Collectors.toList());
  }

  public Optional<ASTWfrSetDefinition> getWfrSetDefinition(String name) {
    return getWfrSetDefinitions()
               .stream()
               .filter(e -> e.getName().equals(name))
               .findFirst();
  }

  public Collection<ASTWfrDefinition> getAllContextConditions(){
    return this.getWfrSetDefinitions().stream()
        .flatMap(d -> d.getWfrDefinitionList().stream())
        .collect(Collectors.toSet());
  }

  public Collection<ASTProvidedGenExtension> getGENProvisionPoints() {
    return getProvisionPoints().stream()
        .filter(e -> e instanceof ASTProvidedGenExtension)
        .map(e -> (ASTProvidedGenExtension) e)
        .collect(Collectors.toList());
  }

  public Collection<ASTRequiredGenExtension> getGENExtensionPoints() {
    return getExtensionPoints().stream()
        .filter(e -> e instanceof ASTRequiredGenExtension)
        .map(e -> (ASTRequiredGenExtension) e)
        .collect(Collectors.toList());
  }

  public Optional<ASTProvidedGenExtension> getGENProvisionPoint(String name) {
    return getGENProvisionPoints()
               .stream()
               .filter(e -> e.getName().equals(name))
               .findFirst();
  }

  public Optional<ASTRequiredGenExtension> getGENExtensionPoint(String name) {
    return getGENExtensionPoints()
               .stream()
               .filter(e -> e.getName().equals(name))
               .findFirst();
  }

  public Collection<ASTImplication> getImplications(){
    return languageComponentElements
               .stream()
               .filter(e -> e instanceof ASTImplication)
               .map(e -> (ASTImplication) e)
               .collect(Collectors.toList());
  }

  public Collection<String> getImpliedEPs(String ppName){
    return getImplications().stream()
        .filter(e -> e.getSource().equals(ppName))
        .flatMap(e -> e.getTargetList().stream())
        .collect(Collectors.toList());
  }

  public ASTGrammarDefinition getGrammarDefinition() {
      return getGrammarDefinition(0);
  }

  /**
   * Calculates the names of all provides points that imply the extension
   * point epName.
   *
   * @param epName The name of the extension point to search for
   * @return The names of provides points implying the extension point.
   */
  public Collection<String> getImplyingPps(String epName){
    return getImplications().stream()
        .filter(e -> e.getTargetList().contains(epName))
        .map(ASTImplication::getSource)
        .collect(Collectors.toSet());
  }

  /**
   * Determine the name of the referenced grammar.
   * @return The name of the referenced grammar.
   */
  public String getReferencedGrammarName() {
    final ASTMCQualifiedName qualifiedName = this.getGrammarDefinition().getMCQualifiedName();
    if(qualifiedName.sizeParts() > 0) {
      return qualifiedName.getPartsList().get(qualifiedName.sizeParts() - 1);
    } else {
      return "";
    }
  }

  /**
   * Determine the package of the referenced grammar.
   * @return The package of the referenced grammar.
   */
  public String getReferencedGrammarPackage() {
    final ASTMCQualifiedName qualifiedName = this.getGrammarDefinition().getMCQualifiedName();
    if(qualifiedName.sizeParts() > 1) {
      return Names.getQualifiedName(
          qualifiedName.getPartsList().subList(0, qualifiedName.sizeParts() - 1)
      );
    } else {
      return "";
    }
  }

  /**
   * Determine the names of the parameters of the component that are used in at least one
   * well-formedness rule in the wfr set with the given name.
   *
   * @param wfrSetName The name of the wfr set to determine the parameters for
   * @return The set of parameters used in the given set.
   * Empty set if there is no wfr set with the given name.
   */
  public Set<String> getWfrParameterNamesForWfrSet(String wfrSetName) {
    Set<String> result = new HashSet<>();

    if(!getWfrSetDefinition(wfrSetName).isPresent()) {
      return result;
    }

    final ASTWfrSetDefinition wfrSet = getWfrSetDefinition(wfrSetName).get();

    final Set<String> referencedRules =
        wfrSet.getWfrDefinitionList().stream()
            .map(c -> c.getWfrReference().toString())
            .collect(Collectors.toSet());

    return this.getParameters().stream()
        .filter(ASTParameter::isWfr)
        .filter(p -> referencedRules.contains(p.getReference().toString()))
        .map(ASTParameter::getName)
        .collect(Collectors.toSet());
  }

  public Optional<ASTParameter> getParameter(String name) {
    return this.getParameters().stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  @Override
  public String toString() {
    return "LC{" +
        "name='" + name + '\'' +
        ", as=" + grammarDefinitions.get(0).getMCQualifiedName().toString() +
        '}';
  }

  public Set<String> getWfrSetNames() {
    return this.getWfrSetDefinitions()
        .stream()
        .map(ASTWfrSetDefinition::getName)
        .collect(Collectors.toSet());
  }

  @Override
  public ASTLanguageComponent deepClone(ASTLanguageComponent result) {
    result = super.deepClone(result);
    result.setEnclosingScope(this.getEnclosingScope());
    return result;
  }
}
