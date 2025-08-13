package sc._generator

import java.util.List
import java.util.Map
import sc.statechart._ast.ASTSCMain

class Statechart2Java {

  def String generate(ASTAutMain ${node}, List<String> ${transitions}, Map<String, String> ${stateName2className}, String ${initialState}) {
    return '''
      import java.util.List;
      import java.util.ArrayList;
      import sc._generator.Transition;
      import sc._generator._product.IState;
      import java.util.Optional;
      import de.se_rwth.commons.logging.Log;


      public class ${node}.name {

        List<Transition> transitions;

        IState currentState = new ${initialState}State();


        ${stateName2className}.get(key) key = new ${stateName2className}.get(key)();

        public ${node}.name() {
          this.transitions = new ArrayList<Transition>();

         ${transitions}
        }

        public void doStep(String input) {
          boolean stateChanged = false;
          for (Transition t : ${transitions}) {
            if (t.getSourceState().getName().equals(currentState.getName())) {
              Optional<IState> target = t.execute(input);
              if (target.isPresent()) {
                currentState = target.get();
                stateChanged = true;
                currentState.visit();
              }
            }
          }
          if (!stateChanged) {
            Log.error("Input " + input + " is not valid in the current state");
            System.exit(1);
          }

        }

      }
    '''
  }

}