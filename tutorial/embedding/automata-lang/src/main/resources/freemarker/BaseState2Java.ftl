import aut._generator.state._product.IState;

public class ${node.name}State implements IState {

    public ${node.name}State() {
    }

    @Override
    public String getName() {
      return "${node.name}";
    }

    @Override
    public void visit() {
      System.out.println("Visited state " + getName());
    }
}
