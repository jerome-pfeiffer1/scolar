package sc.charguard._generator;

import de.monticore.io.FileReaderWriter;
import de.monticore.prettyprint.IndentPrinter;
import sc.character._ast.ASTCharacterRule;

import java.nio.file.Path;

public class CharacterGenerator implements ICharacterGenerator {
  @Override
  public void generate(ASTCharacterRule c, Path path) {
    IndentPrinter printer = new IndentPrinter();
    printer.println("public class " + c.getCharacter().getSource() + " implements sc.charguard._generator.ICharacter" +  " {");
    printer.indent();

    printer.println("private Character character = \'" + c.getCharacter().getSource() + "\';");

    printer.println("public Character getCharacter() {");
    printer.indent();
    printer.println("return character;");
    printer.unindent();
    printer.println("}");

    printer.unindent();
    printer.println("}");

    Path targetPath = path.resolve(c.getCharacter().getSource() + ".java");
    FileReaderWriter.storeInFile(targetPath, printer.getContent());
  }

  @Override
  public Class<?> getTargetInterface() {
    return ICharacter.class;
  }
}
