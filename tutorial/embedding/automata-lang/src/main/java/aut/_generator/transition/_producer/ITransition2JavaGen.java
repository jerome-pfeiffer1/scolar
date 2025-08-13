package aut._generator.transition._producer;

import aut.automatongrammar._ast.ASTTransition;

public interface ITransition2JavaGen {

    public void generate(ASTTransition transition, java.nio.file.Path path);

    public String getTransitionClassName(ASTTransition transition);

    public String getTransitionName(ASTTransition transition);

    public Class<?> getTargetInterface();
}
