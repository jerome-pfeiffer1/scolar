package test;

import java.util.List;
import java.util.ArrayList;
import sc._generator.Transition;
import sc._generator._product.IState;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;


public class TestSCWithCounter {

  List<Transition> transitions;

  IState currentState = new AState();

  AState A = new AState();
  BState B = new BState();
  CStateCounterState CState = new CStateCounterState();
  F F = new F();
  public TestSCWithCounter() {
    this.transitions = new ArrayList<Transition>();

   this.transitions.add(new Transition(new sc._generator.BaseStateDelegator(A), new sc._generator.BaseStateDelegator(B),new sc.charguard._generator.ICharacter2IGuardExpr(new b()), new sc._generator.BaseActionDelegator(new PrintAction())));
   this.transitions.add(new Transition(new sc._generator.BaseStateDelegator(B), new sc.counterstates._generator.ICounterState2IState(CState),new sc.charguard._generator.ICharacter2IGuardExpr(new c()), new sc._generator.BaseActionDelegator(new ExecAction())));
   this.transitions.add(new Transition(new sc.counterstates._generator.ICounterState2IState(CState), new sc.counterstates._generator.ICounterState2IState(CState),new sc.charguard._generator.ICharacter2IGuardExpr(new c()), new sc._generator.BaseActionDelegator(new ExecAction())));
   this.transitions.add(new Transition(new sc.counterstates._generator.ICounterState2IState(CState), new sc.finalstate._generator.IFinalState2IState(F),new sc.charguard._generator.ICharacter2IGuardExpr(new e()), new sc._generator.BaseActionDelegator(new ExecAction())));
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
