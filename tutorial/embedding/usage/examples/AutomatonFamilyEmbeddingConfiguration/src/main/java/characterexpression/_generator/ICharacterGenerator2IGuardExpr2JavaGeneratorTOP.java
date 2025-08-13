package characterexpression._generator;

import characterexpression._generator.*;
import aut._generator.guard._producer.*;

/**
 * Adapter class between producer interface of the extension point ICharacterGenerator and
 * the producer interface of provision point IGuardExpr2JavaGenerator. This adapter has to be extended by a
 * class with name ICharacterGenerator2IGuardExpr2JavaGenerator. Otherwise it cannot be automatically
 * registered by the composed generator.
 */
public abstract class ICharacterGenerator2IGuardExpr2JavaGeneratorTOP implements IGuardExpr2JavaGenerator {

    protected ICharacterGenerator adaptee;

    public ICharacterGenerator2IGuardExpr2JavaGeneratorTOP(ICharacterGenerator adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public Class<?> getTargetInterface() {
        return ICharacterGenerator2IGuardExpr2JavaGenerator.class;
    }
}