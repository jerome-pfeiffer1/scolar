package aut._generator;


import aut._generator.action.BaseActionDelegator;
import aut._generator.state.BaseStateDelegator;
import aut._generator.state._product.IState;
import aut._generator.transition._product.ITransition;
import characterexpression._generator.ICharacterGenerator2IGuardExpr2JavaGenerator;

import java.util.Optional;

/**
 *
 * hwc
 *
 */

public class Transition implements ITransition {

    public BaseStateDelegator start;
    public BaseStateDelegator end;
    public ICharacterGenerator2IGuardExpr2JavaGenerator gen;
    public BaseActionDelegator action;

    public Transition (BaseStateDelegator start, BaseStateDelegator end, ICharacterGenerator2IGuardExpr2JavaGenerator gen, BaseActionDelegator action) {
        this.start = start;
        this.end = end;
        this.gen = gen;
        this.action = action;
    }

    public IState getSourceState() {
        return start;
    }

    public Optional<IState> execute(String input) {
        return Optional.empty();
    }
}
