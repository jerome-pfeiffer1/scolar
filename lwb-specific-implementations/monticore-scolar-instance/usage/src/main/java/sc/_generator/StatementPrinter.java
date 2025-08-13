/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class StatementPrinter implements IActionInterpreter{

  /**
   * @see sc._generator.IActionInterpreter#interpret(java.lang.String)
   */
  @Override
  public void interpret(String action) {
    if(action.equals("EXEC")) {
      System.out.println("FÜHR AUS!!!");
    }
    if(action.equals("PRINT")) {
      System.out.println("PRINT DEINE MUDDA!!!");
    }
    if(action.equals("SEND")) {
      System.out.println("ICH SEND DICH HEIM!!!");
    }
  }
  
}
