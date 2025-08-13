package aut._generator;

import aut._generator.action._producer.IAction2JavaGenerator;
import aut._generator.guard._producer.IGuardExpr2JavaGenerator;
import de.monticore.ast.ASTNode;
import characterexpression._generator.CharacterGenerator;
import characterexpression._generator.ICharacterGenerator2IGuardExpr2JavaGenerator;
import timedexpr._generator.ITimedExprGenerator2IGuardExpr2JavaGenerator;
import timedexpr._generator.TimedExpressionGenerator;

public class AutomatonGrammarWithCharacterExpressionWithClockExpressionGen extends AutGenerator {

    public AutomatonGrammarWithCharacterExpressionWithClockExpressionGen() {
        super();
        // registers a generator producer adapter ICharacterGenerator2IGuardExpr2JavaGenerator for extension point IGuardExpr
        // and passes the concrete generator CharacterGenerator of the provision point CharacterRule as adaptee
        this.register(aut.automatongrammarwithcharacterexpressionwithclockexpression._ast.ASTCharacterRuleIGuardExpr.class,
        new ICharacterGenerator2IGuardExpr2JavaGenerator(new CharacterGenerator()));
        // registers a generator producer adapter ITimedExprGenerator2IGuardExpr2JavaGenerator for extension point IGuardExpr
        // and passes the concrete generator TimedExpressionGenerator of the provision point ClockExpr as adaptee
        this.register(aut.automatongrammarwithcharacterexpressionwithclockexpression._ast.ASTClockExprIGuardExpr.class,
        new ITimedExprGenerator2IGuardExpr2JavaGenerator(new TimedExpressionGenerator()));

    }

    /**
     * Registers a generator for extension point IGuardExpr.
     * The register generator has to implement interface IGuardExpr2JavaGenerator
     *
     */
    public void register(Class<? extends ASTNode> ep, IGuardExpr2JavaGenerator gen) {
        this.getASTIGuardExprGens().put(ep, gen);
    }
    /**
     * Registers a generator for extension point IGuardExpr.
     * The register generator has to implement interface IGuardExpr2JavaGenerator
     *
     */
    public void register(Class<? extends ASTNode> ep, IAction2JavaGenerator gen) {
        this.getASTIActionGens().put(ep, gen);
    }

}

