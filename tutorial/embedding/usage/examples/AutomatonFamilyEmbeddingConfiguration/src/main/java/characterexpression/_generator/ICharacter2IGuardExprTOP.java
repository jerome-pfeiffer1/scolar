package characterexpression._generator;

import characterexpression._generator.*;
import aut._generator.guard._product.*;

/**
 * Adapter class between product interface of the extension point ICharacter and
 * the product interface of provision point IGuardExpr. This adapter has to be extended by a
 * class with name ICharacter2IGuardExpr. Otherwise it cannot be automatically
 * registered by the composed generator.
 */
public abstract class ICharacter2IGuardExprTOP implements IGuardExpr {

    protected ICharacter adaptee;

    public ICharacter2IGuardExprTOP(ICharacter adaptee) {
        this.adaptee = adaptee;
    }

}