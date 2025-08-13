package sc.counterstates._generator;

import de.monticore.io.FileReaderWriter;
import de.monticore.prettyprint.IndentPrinter;
import sc.counterstates._ast.ASTCounterState;

import java.nio.file.Path;

public class CounterStateGenerator implements ICounterStateGenerator{

  @Override
  public void generate(ASTCounterState counterState, Path path) {

    IndentPrinter printer = new IndentPrinter();
    final String className = counterState.getName() + "sc.counterstates._generator.CounterState";
    printer.println("public class " + className + " implements ICounterState" +  " {");
    printer.indent();

    printer.println("private String name = \"" + counterState.getName() + "\";");

    // Print field for counter and step
    int step = counterState.getStepValue().getValue();
    printer.println("private Integer step = " + step + ";");

    int value = counterState.getStartValue().getValue();
    printer.println("private Integer counterValue = " + value + ";");

    // Print getName Method
    printer.println("public String getName() {");
    printer.indent();
    printer.println("return name;");
    printer.unindent();
    printer.println("}");

    // Print getCounterValue
    printer.println("public Integer getCounterValue() {");
    printer.indent();
    printer.println("return counterValue;");
    printer.unindent();
    printer.println("}");

    // Print getStepValue
    printer.println("public Integer getStepValue() {");
    printer.indent();
    printer.println("return step;");
    printer.unindent();
    printer.println("}");

    // Print getStepValue
    printer.println("public void visit() {");
    printer.indent();
    printer.println("this.counter = getStepValue() + getCounterValue();");
    printer.println("System.out.println(\"Current counter for \" + this.getName() + \":\" + getCounterValue());");
    printer.unindent();
    printer.println("}");

    printer.unindent();
    printer.println("}");

    // Output file
    Path targetPath = path.resolve(className + ".java");
    FileReaderWriter.storeInFile(targetPath, printer.getContent());
  }

  @Override
  public Class<?> getTargetInterface() {
    return ICounterState.class;
  }
}
