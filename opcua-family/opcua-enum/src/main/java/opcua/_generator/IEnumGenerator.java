package constraints._generator;

import opcua.enumdatatype._ast.ASTEnum;

import java.nio.file.Path;

public interface IEnumGenerator {

    void generateEnum(ASTEnum enumNode, Path outputPath);

}
