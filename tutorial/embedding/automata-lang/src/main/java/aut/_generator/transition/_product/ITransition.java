package aut._generator.transition._product;

import aut._generator.state._product.IState;

import java.util.Optional;

public interface ITransition {

    public IState getSourceState();

    public Optional<IState> execute(String input);
}
