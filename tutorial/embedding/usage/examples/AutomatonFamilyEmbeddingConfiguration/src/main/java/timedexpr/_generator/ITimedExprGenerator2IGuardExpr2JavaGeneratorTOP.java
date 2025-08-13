package timedexpr._generator;

import timedexpr._generator.*;
import aut._generator.guard._producer.*;

/**
 * Adapter class between producer interface of the extension point ITimedExprGenerator and
 * the producer interface of provision point IGuardExpr2JavaGenerator. This adapter has to be extended by a
 * class with name ITimedExprGenerator2IGuardExpr2JavaGenerator. Otherwise it cannot be automatically
 * registered by the composed generator.
 */
public abstract class ITimedExprGenerator2IGuardExpr2JavaGeneratorTOP implements IGuardExpr2JavaGenerator {

    protected ITimedExprGenerator adaptee;

    public ITimedExprGenerator2IGuardExpr2JavaGeneratorTOP(ITimedExprGenerator adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public Class<?> getTargetInterface() {
        return ITimedExprGenerator2IGuardExpr2JavaGenerator.class;
    }
}