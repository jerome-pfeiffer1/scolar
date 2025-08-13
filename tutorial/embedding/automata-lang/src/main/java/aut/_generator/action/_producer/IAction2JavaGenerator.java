/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator.action._producer;

import aut.automatongrammar._ast.ASTIAction;


/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface IAction2JavaGenerator {
  
  public void generate(ASTIAction expr, java.nio.file.Path path);
  
  public String getActionClassName(ASTIAction node);

  public Class<?> getTargetInterface();
  
}
