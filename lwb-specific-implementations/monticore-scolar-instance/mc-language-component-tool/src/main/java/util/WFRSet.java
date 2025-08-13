/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package util;

import languagecomponentbase._ast.ASTOptionality;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author Michael Mutert
 */
public class WFRSet {
  private String name;
  private String definingComponentName;
  private String grammarName;
  private String grammarPackage;
  private Set<Pair<String, String>> mergedComponentAndSetNames;
  private Set<Parameter> usedParameters;

  public WFRSet(
      String name,
      String definingComponentName,
      String grammarPackage,
      String grammarName) {

    this.name = name;
    this.definingComponentName = definingComponentName;
    this.grammarPackage = grammarPackage;
    this.grammarName = grammarName;
    this.usedParameters = new HashSet<>();
    this.mergedComponentAndSetNames = new HashSet<>();
  }

  public void addParameter(String parameterName, String parameterType, ASTOptionality optionality) {
    usedParameters.add(new Parameter(parameterName, parameterType, optionality));
  }

  public void addParameter(Parameter parameter){
    usedParameters.add(parameter);
  }

  public void mergeSet(String definingComponentName, String setName){
    this.mergedComponentAndSetNames.add(new Pair<>(definingComponentName, setName));
  }

  public Set<Pair<String, String>> getMergedComponentAndSetNames() {
    return new HashSet<>(mergedComponentAndSetNames);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Parameter> getUsedParameters() {
    return usedParameters;
  }

  public String getDefiningComponentName() {
    return definingComponentName;
  }

  public void setDefiningComponentName(String definingComponentName) {
    this.definingComponentName = definingComponentName;
  }

  public String getGrammarPackage() {
    return grammarPackage;
  }

  public String getGrammarName() {
    return grammarName;
  }
}
