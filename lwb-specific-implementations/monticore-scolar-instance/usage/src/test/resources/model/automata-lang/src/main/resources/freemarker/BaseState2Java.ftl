package sc._generator

import sc.statechart._ast.ASTStateBase

class BaseState2Java {

  def generate(${node}) {
    return '''
      public class ${node}.name State implements sc._generator._product.IState {

        public ${node}.nameState() {
        }

        @Override
        public String getName() {
          return "${node}.name";
        }

        @Override
        public void visit() {
          System.out.println("Visited state " + getName());
        }
      }
    '''
  }

}