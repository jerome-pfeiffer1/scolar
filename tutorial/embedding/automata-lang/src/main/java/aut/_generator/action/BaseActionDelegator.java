/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator.action;

import aut._generator.action._product.IAction;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class BaseActionDelegator implements IAction {

  IAction adaptee;
  
  /**
   * Constructor for sc._generator.BaseActionDelegator
   */
  public BaseActionDelegator(IAction adaptee) {
    this.adaptee = adaptee;
  }
  
  /**
   * @see aut._generator.action.product._product.IAction#execute()
   */
  @Override
  public void execute() {
    adaptee.execute();
  }
  
}
