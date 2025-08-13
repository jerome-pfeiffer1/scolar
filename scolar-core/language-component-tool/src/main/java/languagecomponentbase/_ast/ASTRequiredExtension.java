/*
 * Copyright (c) 2020 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase._ast;

public interface ASTRequiredExtension extends ASTRequiredExtensionTOP {
  
  default boolean isMandatory() {
    return ASTOptionality.MANDATORY.equals(getOptionality());
  }
  
  default boolean isOptional() {
    return ASTOptionality.OPTIONAL.equals(getOptionality());
  }

  default boolean isAggregate() {
    return ASTComposition.AGGREGATION.equals(getComposition());
  }

  default boolean isEmbed() {
    return ASTComposition.EMBEDDING.equals(getComposition());
  }
}
