/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package timedexpr.clockexpression.cocos;

import timedexpr._cocos.NoNegativeTime;
import timedexpr.clockexpression._cocos.ClockExpressionCoCoChecker;

public class TimedExpressionCoCos {

  public ClockExpressionCoCoChecker createTimeCorrectnessChecker() {
    ClockExpressionCoCoChecker checker = new ClockExpressionCoCoChecker();

    checker.addCoCo(new NoNegativeTime());

    return checker;
  }
}
