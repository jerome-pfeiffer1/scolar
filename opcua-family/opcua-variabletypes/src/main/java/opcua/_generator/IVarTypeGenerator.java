package opcua._generator;


import opcua.variabletype._ast.ASTVariableTypeDef;

import java.nio.file.Path;

public interface IVarTypeGenerator {

    public void generate(ASTVariableTypeDef artifact, Path outputPath);

}