/*
 * Copyright (c) 2020 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package character._generator;

import characterexpression._generator.ICharacter;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ICharacter2IGuardExpr extends ICharacter2IGuardExprTOP {
  
  /**
   * Constructor for character._generator.ICharacter2IGuardExpr
   * 
   * @param adaptee
   */
  public ICharacter2IGuardExpr(ICharacter adaptee) {
    super(adaptee);
  }
  
  /**
   * @see aut._generator.guard._product.IGuardExpr#eval(java.lang.String)
   */
  @Override
  public boolean eval(String input) {
    // TODO Add actual adapter implementation
    return false;
  }
  
}
