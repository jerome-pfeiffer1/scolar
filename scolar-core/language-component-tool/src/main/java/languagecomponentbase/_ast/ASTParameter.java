/*
 * Copyright (c) 2020 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;


/**
 *
 * @author (last commit) Felix Jordan
 */
public class ASTParameter extends ASTParameterTOP {
  
  protected ASTParameter() {
  }
  
  protected ASTParameter(
          ASTOptionality optionality,
          ASTMCType type,
          String name,
          ASTMCQualifiedName reference,
          boolean wfr,
          boolean transformation
      ) {
    setOptionality(optionality);
    setMCType(type);
    setName(name);
    setReference(reference);
    setWfr(wfr);
    setTransformation(transformation);
  }
  
  public boolean isMandatory() {
    return ASTOptionality.MANDATORY.equals(getOptionality());
  }
  
  public boolean isOptional() {
    return ASTOptionality.OPTIONAL.equals(getOptionality());
  }
}
