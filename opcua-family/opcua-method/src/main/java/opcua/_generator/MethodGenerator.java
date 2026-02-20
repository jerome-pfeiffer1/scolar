package constraints._generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import opcua.methodobjecttypeelem._ast.ASTMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MethodGenerator implements IMethodGenerator {

    public void generateMethod(ASTMethod method, Path outputPath) {
        GeneratorSetup setup = new GeneratorSetup();
        setup.setOutputDirectory(outputPath.toFile());
        setup.setTracing(false);
        GeneratorEngine engine = new GeneratorEngine(setup);
        StringBuilder generate = engine.generate("freemarker/Method.ftl", method);
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + method.getName() + ".py");
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
