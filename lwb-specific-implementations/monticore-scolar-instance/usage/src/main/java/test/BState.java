package test;

public class BState implements sc._generator._product.IState {
  
  public BState() {
  }
  
  @Override
  public String getName() {
    return "B";
  }

  @Override
  public void visit() {
    System.out.println("Visited state " + getName());
  }
}
