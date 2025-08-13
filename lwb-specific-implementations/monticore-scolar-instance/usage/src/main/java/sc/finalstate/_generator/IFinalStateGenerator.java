package sc.finalstate._generator;

import java.nio.file.Path;

public interface IFinalStateGenerator {
  void generate(sc.finalstate._ast.ASTFState fs, java.nio.file.Path path);

  Class<?> getTargetInterface();
}
