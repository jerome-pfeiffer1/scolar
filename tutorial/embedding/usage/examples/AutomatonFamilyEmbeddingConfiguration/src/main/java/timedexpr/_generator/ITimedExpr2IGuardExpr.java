package timedexpr._generator;

import characterexpression._generator.ICharacter;
import characterexpression._generator.ICharacter2IGuardExprTOP;

/**
* hwc
 */

public class ITimedExpr2IGuardExpr extends ITimedExpr2IGuardExprTOP {

    public ITimedExpr2IGuardExpr(ITimedExpr adaptee) {
        super(adaptee);
    }

    @Override
    public boolean eval(String input) {
        return false;
    }
}
