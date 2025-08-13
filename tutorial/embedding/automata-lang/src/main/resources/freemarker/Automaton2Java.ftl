import java.util.List;
import java.util.ArrayList;
import aut._generator.Transition;
import aut._generator.state._product.IState;
import characterexpression._generator.ICharacterGenerator;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;

public class ${node.name} implements aut._generator._product.IAutomaton {

List<Transition> transitions;

	IState currentState = new ${initialState}State();

	<#list stateName2className?keys as key>
		${stateName2className[key]} ${key} = new ${stateName2className[key]}();
	</#list>

	public ${node.name}() {
		this.transitions = new ArrayList<Transition>();

		<#list transitions as t>
		this.transitions.add(${t});
		</#list>
	}

	public void doStep(String input) {
		boolean stateChanged = false;
		for (Transition t : transitions) {
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
