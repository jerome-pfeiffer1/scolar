package sc;

import de.monticore.ast.ASTNode;

public class StatechartWithFinalStateWithCharacterGen extends sc._generator.SCGenerator {
  
 
  public StatechartWithFinalStateWithCharacterGen() {
    super();
    this.register(sc.statechartwithfinalstatewithcharacter._ast.ASTFStateIState.class, new sc.finalstate._generator.IFinalStateGenerator2IState2JavaGen(new sc.finalstate._generator.FinalStateGenerator()));
    this.register(sc.statechartwithfinalstatewithcharacter._ast.ASTCharacterRuleIGuardExpr.class, new sc.charguard._generator.ICharacterGenerator2IGuardExpr2JavaGenerator(new sc.charguard._generator.CharacterGenerator()));
  }
 
 
   public void register(Class<? extends ASTNode> ep, sc._generator._producer.IState2JavaGen gen) {
     this
     .getASTIStateGens().put(ep, gen);
    
   }
 
   public void register(Class<? extends ASTNode> ep, sc._generator._producer.IGuardExpr2JavaGenerator gen) {
     this
     .getASTIGuardExprGens().put(ep, gen);
    
   }
 
  }   
