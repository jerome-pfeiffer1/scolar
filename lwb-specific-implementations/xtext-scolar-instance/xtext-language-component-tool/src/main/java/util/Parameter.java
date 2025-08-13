package util;

import languagecomponentbase._ast.ASTOptionality;

import java.util.Objects;

public class Parameter {

  private String name;
  private String typeAsString;
  private boolean isOptional;

  public Parameter(String name, String typeAsString, ASTOptionality optionality) {
    this.name = name;
    this.typeAsString = typeAsString;
    this.isOptional = optionality.equals(ASTOptionality.OPTIONAL);
  }

  public Parameter(String name, String typeAsString, boolean isOptional) {
    this.name = name;
    this.typeAsString = typeAsString;
    this.isOptional = isOptional;
  }

  public String getName() {
    return name;
  }

  public String getTypeAsString() {
    return typeAsString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Parameter)) {
      return false;
    }
    final Parameter parameter = (Parameter) o;
    return Objects.equals(name, parameter.name) &&
        Objects.equals(typeAsString, parameter.typeAsString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, typeAsString);
  }

  public boolean isOptional() {
    return isOptional;
  }
}
