/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator.state._producer;

import aut.automatongrammar._ast.ASTIState;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface IState2JavaGen {
  
  public void generate(ASTIState node, java.nio.file.Path path);
  
  public String getStateClassName(ASTIState state);
  
  public String getStateName(ASTIState state);
 
  public Class<?> getTargetInterface();

}
