package constraints._generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import opcua.structdatatype._ast.ASTParameter;
import opcua.structdatatype._ast.ASTStruct;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StructGenerator implements IStructGenerator {

    public void generateStruct(ASTStruct struct, Path outputPath) {
        GeneratorSetup setup = new GeneratorSetup();
        setup.setOutputDirectory(outputPath.toFile());
        setup.setTracing(false);
        GeneratorEngine engine = new GeneratorEngine(setup);
        List<ASTParameter> parameterList = struct.getParameterList();
        StringBuilder generate = engine.generate("freemarker/Struct.ftl", struct, parameterList);
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + struct.getName() + "DataType.py");
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
