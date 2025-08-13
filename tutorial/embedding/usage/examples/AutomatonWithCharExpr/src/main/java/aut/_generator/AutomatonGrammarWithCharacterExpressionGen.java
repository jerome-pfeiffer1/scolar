package aut._generator;

import aut._generator.guard._producer.IGuardExpr2JavaGenerator;
import characterexpression._generator.ICharacterGenerator2IGuardExpr2JavaGenerator;
import de.monticore.ast.ASTNode;
import characterexpression._generator.CharacterGenerator;
import aut._generator.AutGenerator;

public class AutomatonGrammarWithCharacterExpressionGen extends AutGenerator {

    public AutomatonGrammarWithCharacterExpressionGen() {
        super();
        // registers a generator producer adapter ICharacterGenerator2IGuardExpr2JavaGenerator for extension point IGuardExpr
        // and passes the concrete generator CharacterGenerator of the provision point CharacterRule as adaptee
        this.register(aut.automatongrammarwithcharacterexpression._ast.ASTCharacterRuleIGuardExpr.class,
                new ICharacterGenerator2IGuardExpr2JavaGenerator(new CharacterGenerator()));
    }

    /**
     * Registers a generator for extension point IGuardExpr.
     * The register generator has to implement interface IGuardExpr2JavaGenerator
     *
     */
    public void register(Class<? extends ASTNode> ep, IGuardExpr2JavaGenerator gen) {
        this
                .getASTIGuardExprGens().put(ep, gen);

    }

}

