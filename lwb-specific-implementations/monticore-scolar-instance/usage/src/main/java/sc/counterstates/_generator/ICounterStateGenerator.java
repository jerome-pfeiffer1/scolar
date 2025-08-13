package sc.counterstates._generator;

public interface ICounterStateGenerator {

  void generate(sc.counterstates._ast.ASTCounterState counterState, java.nio.file.Path path);

  Class<?> getTargetInterface();
}
