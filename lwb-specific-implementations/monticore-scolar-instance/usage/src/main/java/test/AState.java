package test;

public class AState implements sc._generator._product.IState {
  
  public AState() {
  }
  
  @Override
  public String getName() {
    return "A";
  }

  @Override
  public void visit() {
    System.out.println("Visited state " + getName());
  }
}
