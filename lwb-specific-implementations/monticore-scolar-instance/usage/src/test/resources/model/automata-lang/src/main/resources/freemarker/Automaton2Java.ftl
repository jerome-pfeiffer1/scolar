package aut._generator

import java.util.List
import java.util.Map
import aut.automaton._ast.ASTAutMain

class Automaton2Java {

  def String generate(ASTAutMain ${node}, List<String> ${transitions}, Map<String, String> ${stateName2className}, String ${initialState}) {
    return '''
			import java.util.List;
			import java.util.ArrayList;
			import sc._generator.Transition;
			import sc._generator._product.IState;
			import java.util.Optional;
			import de.se_rwth.commons.logging.Log;


			public class ${node}.name implements aut._generator.product.IAutomaton {

			  List<Transition> transitions;

			  IState currentState = new ${initialState}State();

			  «#list stateName2className.keySet as key»
				${stateName2className}.get(key) key = new ${stateName2className}.get(key)();
			  «/#list»
			  public «node.name»() {
			  this.transitions = new ArrayList<Transition>();

			   «#list transitions as t»
			   	this.transitions.add(t);
			   «/#list»
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
