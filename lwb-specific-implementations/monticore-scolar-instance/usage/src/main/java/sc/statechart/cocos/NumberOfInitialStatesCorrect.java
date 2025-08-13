/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc.statechart.cocos;

import de.se_rwth.commons.logging.Log;
import sc.statechart._ast.ASTStateBase;
import sc.statechart._cocos.StatechartASTStateBaseCoCo;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class NumberOfInitialStatesCorrect implements StatechartASTStateBaseCoCo {
  
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
