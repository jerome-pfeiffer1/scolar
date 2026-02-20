package constraints._generator;

import constraints.constraint.ConstraintMill;
import constraints.constraint._ast.ASTConstraintDef;
import constraints.constraint._ast.ASTExpression;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConstraintGenerator implements IConstraintGenerator {

    public void generateConstraintChecker(ASTConstraintDef constraint, Path outputPath) {
        GeneratorSetup setup = new GeneratorSetup();
        setup.setOutputDirectory(outputPath.toFile());
        setup.setTracing(false);
        GeneratorEngine engine = new GeneratorEngine(setup);
        StringBuilder builder = new StringBuilder();
        for (ASTExpression astExpression : constraint.getExpressionList()) {
            builder.append(ConstraintMill.prettyPrint(astExpression, false));
            builder.append("\n");
        }

        StringBuilder generate = engine.generate("freemarker/Constraint.ftl", constraint, builder.toString());
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + constraint.getName() + ".py");
            Files.createDirectories(path.getParent());
            FileWriter fw = new FileWriter(path.toFile());
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
