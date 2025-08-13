/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator._producer;

import aut.automatongrammar._ast.ASTAutMain;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 */
public interface IAutGenerator {
  
  public void generate(ASTAutMain node, java.nio.file.Path path);

  public Class<?> getTargetInterface();
  
}
