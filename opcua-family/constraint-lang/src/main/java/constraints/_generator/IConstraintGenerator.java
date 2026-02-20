package constraints._generator;


import constraints.constraint._ast.ASTConstraintDef;

import java.nio.file.Path;

public interface IConstraintGenerator {

    void generateConstraintChecker(ASTConstraintDef struct, Path outputPath);
}
