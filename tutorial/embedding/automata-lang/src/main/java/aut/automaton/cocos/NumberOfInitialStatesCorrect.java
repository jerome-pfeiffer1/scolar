/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut.automaton.cocos;

import aut.automatongrammar._ast.ASTStateBase;
import aut.automatongrammar._cocos.AutomatonGrammarASTStateBaseCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class NumberOfInitialStatesCorrect implements AutomatonGrammarASTStateBaseCoCo {
  
  private int counter = 0;
  
  private Integer maxNumberOfStates = 5;
  
  @Override
  public void check(ASTStateBase node) {
    if (node.isInitial()) {
      counter += 1;
      if (counter > maxNumberOfStates) {
        Log.error("Too many initial states!");
      }
    }
  }
  
  /**
   * @param maxNumberOfStates the maxNumberOfStates to set
   */
  public void setMaxNumberOfStates(Integer maxNumberOfStates) {
    this.maxNumberOfStates = maxNumberOfStates;
  }
  
}
