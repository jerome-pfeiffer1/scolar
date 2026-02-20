package constraints._generator;

import opcua.structdatatype._ast.ASTStruct;

import java.nio.file.Path;

public interface IStructGenerator {

    void generateStruct(ASTStruct struct, Path outputPath);
}
