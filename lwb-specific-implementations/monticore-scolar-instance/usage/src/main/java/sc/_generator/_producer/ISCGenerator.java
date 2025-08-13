/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator._producer;

import java.io.IOException;
import java.nio.file.Path;

import sc.statechart._ast.ASTIGuardExpr;
import sc.statechart._ast.ASTSCMain;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface ISCGenerator {
  
  public void generate(ASTSCMain node, java.nio.file.Path path) throws IOException;

  public Class<?> getTargetInterface();
  
}
