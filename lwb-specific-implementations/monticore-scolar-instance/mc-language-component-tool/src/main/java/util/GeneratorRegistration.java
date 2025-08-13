/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import languagecomponentbase._ast.ASTParameter;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class GeneratorRegistration {
  
  // This is key host generator
  private String epGenerator;
  
  private String epProducerInterface;
  
  private String epProdReference;
  private String ppProdReference;
  
  private String epAstClass;
  
  private String ppAstClass;
  
  private String ppProducerInterface;
  
  private String ppGenerator;
  
  private Set<ASTParameter> params = new HashSet<>(); 
  
  private Deque<GeneratorRegistration> parentGen;

  private String epPackage;
  private String ppPackage;
  
  /**
   * Constructor for util.GeneratorRegistration
   */
  public GeneratorRegistration(
      String epAstClass,
      String ppAstClass,
      String epGenerator,
      String ppGenerator,
      String epProducerInterface,
      String ppProducerInterface,
      String epProdReference,
      String ppProdReference,
      Set<ASTParameter> params
      ) {
    parentGen = new ArrayDeque<GeneratorRegistration>();
    this.epGenerator = epGenerator;
    this.ppGenerator = ppGenerator;
    this.epProducerInterface = epProducerInterface;
    this.epAstClass = epAstClass;
    this.ppAstClass = ppAstClass;
    this.ppProducerInterface = ppProducerInterface;
    this.epProdReference = epProdReference;
    this.ppProdReference = ppProdReference;
    this.params = params;
  }

  public GeneratorRegistration(
          String epAstClass,
          String ppAstClass,
          String epGenerator,
          String ppGenerator,
          String epProducerInterface,
          String ppProducerInterface,
          String epProdReference,
          String ppProdReference,
          Set<ASTParameter> params,
          String epPackage,
          String ppPackage
  ) {
    parentGen = new ArrayDeque<GeneratorRegistration>();
    this.epGenerator = epGenerator;
    this.ppGenerator = ppGenerator;
    this.epProducerInterface = epProducerInterface;
    this.epAstClass = epAstClass;
    this.ppAstClass = ppAstClass;
    this.ppProducerInterface = ppProducerInterface;
    this.epProdReference = epProdReference;
    this.ppProdReference = ppProdReference;
    this.params = params;
    this.epPackage = epPackage;
    this.ppPackage = ppPackage;
  }
  
  public void addParentGen(GeneratorRegistration reg) {
    parentGen.push(reg);
  }
  
  /**
   * @return params
   */
  public Set<ASTParameter> getParams() {
    return this.params;
  }
  
  /**
   * @return epProdReference
   */
  public String getEpProdReference() {
    return this.epProdReference;
  }
  
  /**
   * @return ppProdReference
   */
  public String getPpProdReference() {
    return this.ppProdReference;
  }
  
  /**
   * @return epGenerator
   */
  public String getEpGenerator() {
    return this.epGenerator;
  }
  
  /**
   * @return parentGen
   */
  public Deque<GeneratorRegistration> getParentGen() {
    return this.parentGen;
  }
  
  /**
   * @return ppProducerInterface
   */
  public String getPpProducerInterface() {
    return this.ppProducerInterface;
  }
  
  /**
   * @return ppAstClass
   */
  public String getPpAstClass() {
    return this.ppAstClass;
  }
  
  /**
   * @return epAstClass
   */
  public String getEpAstClass() {
    return this.epAstClass;
  }
  
  /**
   * @return ppGenerator
   */
  public String getPpGenerator() {
    return this.ppGenerator;
  }
  
  /**
   * @return epProducerInterface
   */
  public String getEpProducerInterface() {
    return this.epProducerInterface;
  }

  public String getEpPackage() {
    return epPackage;
  }

  public String getPpPackage() {
    return ppPackage;
  }

  public boolean containsParam(String string) {
    return this.params.stream().filter(p -> p.getName().equals(string)).findAny().isPresent();
  }
  
}
