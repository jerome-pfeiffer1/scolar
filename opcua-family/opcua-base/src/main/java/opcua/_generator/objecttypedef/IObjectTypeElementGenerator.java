package constraints._generator.objecttypedef;

import opcua.opcua._ast.ASTObjectTypeDefElements;

import java.nio.file.Path;

public interface IObjectTypeElementGenerator {
    void generate(ASTObjectTypeDefElements element, Path outputPath);
}
