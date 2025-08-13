package characterexpression._generator;

import java.nio.file.Path;

import characterexpression.characterexpression._ast.ASTCharacterRule;

public interface ICharacterGenerator {

  void generate(ASTCharacterRule c, Path path);

  Class<?> getTargetInterface();
}
