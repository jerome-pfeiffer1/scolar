package sc;

import de.monticore.ast.ASTNode;

public class StatechartWithFinalStateWithCharacterWithCounterStatesGen extends StatechartWithFinalStateWithCharacterGen{
  
 
  public StatechartWithFinalStateWithCharacterWithCounterStatesGen() {
    super();
    this.register(sc.statechartwithfinalstatewithcharacterwithcounterstates._ast.ASTCounterStateIState.class, new sc.counterstates._generator.ICounterStateGenerator2IState2JavaGen(new sc.counterstates._generator.CounterStateGenerator()));
  }
 
 
   public void register(Class<? extends ASTNode> ep, sc._generator._producer.IState2JavaGen gen) {
     this
     .getASTIStateGens().put(ep, gen);
    
   }
 
  }   
