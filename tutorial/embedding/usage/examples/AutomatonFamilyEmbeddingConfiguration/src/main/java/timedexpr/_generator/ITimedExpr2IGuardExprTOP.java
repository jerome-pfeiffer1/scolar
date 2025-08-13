package timedexpr._generator;

import timedexpr._generator.*;
import aut._generator.guard._product.*;

/**
 * Adapter class between product interface of the extension point ITimedExpr and
 * the product interface of provision point IGuardExpr. This adapter has to be extended by a
 * class with name ITimedExpr2IGuardExpr. Otherwise it cannot be automatically
 * registered by the composed generator.
 */
public abstract class ITimedExpr2IGuardExprTOP implements IGuardExpr {

    protected ITimedExpr adaptee;

    public ITimedExpr2IGuardExprTOP(ITimedExpr adaptee) {
        this.adaptee = adaptee;
    }

}