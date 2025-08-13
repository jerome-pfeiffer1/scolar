/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator._producer;

import java.nio.file.Path;

import sc.statechart._ast.ASTIGuardExpr;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface IGuardExpr2JavaGenerator {
  
  public void generate(ASTIGuardExpr expr, java.nio.file.Path path);
  
  public String getGuardExprClassName(ASTIGuardExpr node);
  
  public Class<?> getTargetInterface();
  
}
