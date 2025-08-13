/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package timedexpr._cocos;

import de.se_rwth.commons.logging.Log;
import timedexpr.clockexpression._ast.ASTTime;
import timedexpr.clockexpression._cocos.ClockExpressionASTTimeCoCo;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class NoNegativeTime implements ClockExpressionASTTimeCoCo {

  /**
   * @see timedexpr.clockexpression._cocos.ClockExpressionASTTimeCoCo#check(timedexpr.clockexpression._ast.ASTTime)
   */
  @Override
  public void check(ASTTime node) {
    if(node.getHours().getValue() < 0 || node.getMinutes().getValue() < 0) {
      Log.error("The given time must not be negative.");
    }
  }
  
}
