package constraints._generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import opcua.enumdatatype._ast.ASTEnum;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnumGenerator implements IEnumGenerator {

    public void generateEnum(ASTEnum enumNode, Path outputPath) {
        GeneratorSetup setup = new GeneratorSetup();
        setup.setOutputDirectory(outputPath.toFile());
        setup.setTracing(false);
        GeneratorEngine engine = new GeneratorEngine(setup);
        StringBuilder generate = engine.generate("freemarker/Enum.ftl", enumNode);
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + enumNode.getName() + ".py");
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
