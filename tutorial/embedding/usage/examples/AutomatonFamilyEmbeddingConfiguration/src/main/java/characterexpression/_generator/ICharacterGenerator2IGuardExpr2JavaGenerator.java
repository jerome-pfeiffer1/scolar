package characterexpression._generator;

import aut.automatongrammar._ast.ASTIGuardExpr;
import characterexpression.characterexpression._ast.ASTCharacterRule;

import java.nio.file.Path;

/**
 * hwc
 */

public class ICharacterGenerator2IGuardExpr2JavaGenerator extends ICharacterGenerator2IGuardExpr2JavaGeneratorTOP {

    public ICharacterGenerator2IGuardExpr2JavaGenerator(ICharacterGenerator adaptee) {
        super(adaptee);
    }

    @Override
    public void generate(ASTIGuardExpr expr, Path path) {
        if(expr instanceof  ASTCharacterRule) {
            adaptee.generate((ASTCharacterRule) expr, path);
        }
    }

    @Override
    public String getGuardExprClassName(ASTIGuardExpr node) {
        return ((ASTCharacterRule) node).getCharacter().getSource();
    }
}
