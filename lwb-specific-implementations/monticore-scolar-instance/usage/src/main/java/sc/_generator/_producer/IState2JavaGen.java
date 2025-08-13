/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator._producer;

import java.io.IOException;
import java.nio.file.Path;

import sc.statechart._ast.ASTIState;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface IState2JavaGen {
  
  public void generate(ASTIState node, java.nio.file.Path path) throws IOException;
  
  public String getStateClassName(ASTIState state);
  
  public String getStateName(ASTIState state);
 
  public Class<?> getTargetInterface();

}
