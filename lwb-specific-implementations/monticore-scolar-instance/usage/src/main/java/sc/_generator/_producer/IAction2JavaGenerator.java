/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator._producer;

import java.io.IOException;
import java.nio.file.Path;

import sc.statechart._ast.ASTIAction;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface IAction2JavaGenerator {
  
  public void generate(ASTIAction expr, java.nio.file.Path path) throws IOException;
  
  public String getActionClassName(ASTIAction node);

  public Class<?> getTargetInterface();
  
}
