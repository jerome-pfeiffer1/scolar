package util;

public class Binding {

  private final BindingType bindingType;
  private final String provisionPoint;
  private final String extensionPoint;

  public Binding(BindingType bindingType, String provisionPoint, String extensionPoint){

    this.bindingType = bindingType;
    this.provisionPoint = provisionPoint;
    this.extensionPoint = extensionPoint;
  }

  public String getExtensionPoint() {
    return extensionPoint;
  }

  public String getProvisionPoint() {
    return provisionPoint;
  }

  public BindingType getBindingType() {
    return bindingType;
  }

  public enum BindingType {
    AS, WFR, GEN
  }
  
  @Override
  public String toString() {
    return bindingType + "-Binding{"
        + "pp=" + provisionPoint
        + ",ep=" + extensionPoint
        + "}";
  }
}
