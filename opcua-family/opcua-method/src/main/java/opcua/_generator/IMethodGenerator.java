package constraints._generator;

import opcua.methodobjecttypeelem._ast.ASTMethod;

import java.nio.file.Path;

public interface IMethodGenerator {

    void generateMethod(ASTMethod method, Path outputPath);

}
