/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator;

import java.util.Optional;

import sc._generator._product.IAction;
import sc._generator._product.IGuardExpr;
import sc._generator._product.IState;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class Transition {
  
  IState sourceState;
  IState targetState;
  
  IGuardExpr g;
  IAction a;

  
  public Transition(IState sourceState, IState targetState, IGuardExpr g, IAction a) {
    this.sourceState = sourceState;
    this.targetState = targetState;
    this.g = g;
    this.a = a;
  }
  
  /**
   * TODO: Write me!
   * @return
   */
  public IState getSourceState() {
    return sourceState;
  }

  /**
   * TODO: Write me!
   * @param input
   */
  public Optional<IState> execute(String input) {
    if(g.eval(input)) {
      a.execute();
      return Optional.of(targetState);
    }
    return Optional.empty();
  }
  
}
