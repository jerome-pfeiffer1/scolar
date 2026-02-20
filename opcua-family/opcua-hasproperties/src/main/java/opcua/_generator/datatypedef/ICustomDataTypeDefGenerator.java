package constraints._generator.datatypedef;


import opcua.opcua._ast.ASTCustomDataTypeDef;

import java.nio.file.Path;

public interface ICustomDataTypeDefGenerator {
    void generate(ASTCustomDataTypeDef element, Path outputPath);
}
