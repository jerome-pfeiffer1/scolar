package test;

public class TestMain {

  public static void main(String[] args) {
    TestSCWithCounter sc = new TestSCWithCounter();
    sc.doStep("b");
    sc.doStep("c");
    sc.doStep("c");
    sc.doStep("c");
    sc.doStep("c");
    sc.doStep("e");
  }
}
