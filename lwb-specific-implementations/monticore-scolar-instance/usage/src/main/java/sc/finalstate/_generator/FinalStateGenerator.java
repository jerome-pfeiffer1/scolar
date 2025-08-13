package sc.finalstate._generator;

import de.monticore.io.FileReaderWriter;
import de.monticore.prettyprint.IndentPrinter;
import sc.finalstate._ast.ASTFState;

import java.nio.file.Path;

public class FinalStateGenerator implements IFinalStateGenerator {

  @Override
  public void generate(ASTFState fs, Path path) {

    IndentPrinter printer = new IndentPrinter();
    printer.println("public class " + fs.getName() + " implements sc.finalstate._generator.IFinalState" +  " {");
    printer.indent();

    printer.println("private String name = \"" + fs.getName() + "\";");

    printer.println("public String getName() {");
    printer.indent();
    printer.println("return name;");
    printer.unindent();
    printer.println("}");

    printer.println("public void terminate() {");
    printer.indent();
    printer.println("System.out.println(\"Reached final state \" + getName());");
    printer.println("System.exit(0);");
    printer.unindent();
    printer.println("}");

    printer.unindent();
    printer.println("}");

    Path targetPath = path.resolve(fs.getName() + ".java");
    FileReaderWriter.storeInFile(targetPath, printer.getContent());
  }

  @Override
  public Class<?> getTargetInterface() {
    return IFinalState.class;
  }
}
