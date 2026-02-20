package opcua._generator;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import opcua.variabletype._ast.ASTVariableTypeDef;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class VarTypeGenerator implements IVarTypeGenerator {


    @Override
    public void generate(ASTVariableTypeDef variable, Path outputPath) {
        GeneratorSetup setup = new GeneratorSetup();
        setup.setOutputDirectory(outputPath.toFile());
        setup.setTracing(false);
        GeneratorEngine engine = new GeneratorEngine(setup);
        StringBuilder generate = engine.generate("freemarker/VariableType.ftl", variable);
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + variable.getName() + "VariableType.py");
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
