package test;

public class CStateCounterState implements sc.counterstates._generator.ICounterState {
  private String name = "CState";
  private Integer step = 2;
  private Integer counterValue = 0;
  public String getName() {
    return name;
  }
  public Integer getCounterValue() {
    return counterValue;
  }
  public Integer getStepValue() {
    return step;
  }
  public void visit() {
    this.counterValue = getStepValue() + getCounterValue();
    System.out.println("Current counter for " + this.getName() + ":" + getCounterValue());
  }
}
