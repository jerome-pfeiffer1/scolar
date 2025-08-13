/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator;

import sc._generator._product.IState;
import sc.statechart._ast.ASTStateBase;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class BaseStateDelegator implements IState {
  
  private IState delegator;
  
  /**
   * Constructor for sc.generator.BaseStateDelegator
   */
  public BaseStateDelegator(IState delegator) {
    this.delegator = delegator;    
  }
  
  /**
   * @see sc._generator._product.IState#getName()
   */
  @Override
  public String getName() {
    return delegator.getName();
  }

  @Override
  public void visit() {
    delegator.visit();
  }


}
