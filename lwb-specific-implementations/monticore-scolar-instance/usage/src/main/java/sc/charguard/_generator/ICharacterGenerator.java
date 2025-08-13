package sc.charguard._generator;

import java.nio.file.Path;

public interface ICharacterGenerator {

  void generate(sc.character._ast.ASTCharacterRule c, java.nio.file.Path path);

  Class<?> getTargetInterface();
}
