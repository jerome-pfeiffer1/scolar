package test;

public class F implements sc.finalstate._generator.IFinalState {
  private String name = "F";
  public String getName() {
    return name;
  }
  public void terminate() {
    System.out.println("Reached final state " + getName());
    System.exit(0);
  }
}
