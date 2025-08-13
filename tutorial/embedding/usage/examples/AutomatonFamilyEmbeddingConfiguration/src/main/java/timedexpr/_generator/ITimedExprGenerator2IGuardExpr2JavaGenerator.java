package timedexpr._generator;

import aut.automatongrammar._ast.ASTIGuardExpr;
import characterexpression._generator.ICharacterGenerator;
import characterexpression._generator.ICharacterGenerator2IGuardExpr2JavaGeneratorTOP;
import characterexpression.characterexpression._ast.ASTCharacterRule;
import timedexpr.clockexpression._ast.ASTClockExpr;

import java.nio.file.Path;

/**
 * hwc
 */

public class ITimedExprGenerator2IGuardExpr2JavaGenerator extends ITimedExprGenerator2IGuardExpr2JavaGeneratorTOP {

    public ITimedExprGenerator2IGuardExpr2JavaGenerator(ITimedExprGenerator adaptee) {
        super(adaptee);
    }

    @Override
    public void generate(ASTIGuardExpr expr, Path path) {
        if(expr instanceof ASTClockExpr) {
            adaptee.generate((ASTClockExpr) expr, path);
        }
    }

    @Override
    public String getGuardExprClassName(ASTIGuardExpr node) {

        String name = "";

        if (((ASTClockExpr) node).isEarlier()) {
            name += "Before";
        }
        else {
            name += "After";
        }
        name += ((ASTClockExpr) node).getTime().getHours().getSource() + "" + ((ASTClockExpr) node).getTime().getMinutes().getSource();
        return name;
    }


}

