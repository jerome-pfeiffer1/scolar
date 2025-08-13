/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package timedexpr._generator;

import java.nio.file.Path;

import timedexpr.clockexpression._ast.ASTClockExpr;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public interface ITimedExprGenerator {
  void generate(ASTClockExpr c, Path path);

  Class<?> getTargetInterface();
}
