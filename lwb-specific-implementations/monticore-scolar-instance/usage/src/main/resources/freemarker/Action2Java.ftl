package sc._generator;

import sc.statechart._ast.ASTAction

class Action2Java {

  def generate(${expr}) {
    return '''
      «IF ${expr}.execOpt.present»
              class ExecAction implements sc._generator._product.IAction {
                @Override
                public void execute() {
                  System.out.println("EXEC");
                }

              }
            «ELSEIF ${expr}.printOpt.present»
              class PrintAction implements sc._generator._product.IAction {
                @Override
                public void execute() {
                  System.out.println("PRINT");
                }

              }
            «ELSE»
              class SendAction implements sc._generator._product.IAction {
                @Override
                public void execute() {
                  System.out.println("SEND");
                }

              }
            «ENDIF»


          '''


  }

}