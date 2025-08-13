/*
 * Copyright (c) 2020 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package character._generator;

import java.nio.file.Path;

import aut.automatongrammar._ast.ASTIGuardExpr;
import characterexpression._generator.ICharacterGenerator;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ICharacterGenerator2IGuardExpr2JavaGenerator
    extends ICharacterGenerator2IGuardExpr2JavaGeneratorTOP {
  
  /**
   * Constructor for
   * character._generator.ICharacterGenerator2IGuardExpr2JavaGenerator
   * 
   * @param adaptee
   */
  public ICharacterGenerator2IGuardExpr2JavaGenerator(ICharacterGenerator adaptee) {
    super(adaptee);
  }
  
  /**
   * @see aut._generator.guard._producer.IGuardExpr2JavaGenerator#generate(aut.automaton._ast.ASTIGuardExpr,
   * java.nio.file.Path)
   */
  @Override
  public void generate(ASTIGuardExpr expr, Path path) {
    // TODO Add actual adapter implementation
  }
  
  /**
   * @see aut._generator.guard._producer.IGuardExpr2JavaGenerator#getGuardExprClassName(aut.automaton._ast.ASTIGuardExpr)
   */
  @Override
  public String getGuardExprClassName(ASTIGuardExpr node) {
    // TODO Add actual adapter implementation
    return null;
  }
  
}
