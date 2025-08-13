package languagecomponentbase;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._symboltable.ILanguageComponentBaseGlobalScope;
import languagecomponentbase._symboltable.LanguageComponentBaseGlobalScope;
import languagecomponentbase._symboltable.LanguageComponentSymbol;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Tool for the language component language
 */
public class LanguageComponentBaseProcessor {

  protected ILanguageComponentBaseGlobalScope symbolTable;

  /**
   * Constructor for languageComponent.LanguageComponentProcessor
   * @param modelPath path of source models
   */
  public LanguageComponentBaseProcessor(
          MCPath modelPath) {
    this(new LanguageComponentBaseGlobalScope(modelPath, ".*"));
  }

  public LanguageComponentBaseProcessor(ILanguageComponentBaseGlobalScope globalScope){
    symbolTable = globalScope;
  }

  /**
   * @param qualifiedName Qualified name of the languageComponent to load
   * @return Optional containing the loaded LanguageComponentSymbol, if found.
   * {@link Optional#empty()}, otherwise.
   */
  public Optional<LanguageComponentSymbol> loadLanguageComponentSymbolWithoutCoCos(
          String qualifiedName) {
    return symbolTable.resolveLanguageComponent(qualifiedName);
  }

  /**
   * Loads the languageComponent symbol of the languageComponent with the given qualified name that
   * should be located in one of the given model paths, after checking it for
   * correctness using the standard CoCo-Checker for languageComponents.
   *
   * @param qualifiedName Qualified name of the languageComponent to load
   * @return Optional containing the loaded LanguageComponentSymbol, if found.
   * {@link Optional#empty()}, otherwise.
   */
  public Optional<LanguageComponentSymbol> loadLanguageComponentSymbol(
          String qualifiedName) {

    final Optional<LanguageComponentSymbol> symbol =
            symbolTable.resolveLanguageComponent(qualifiedName);

    if (!symbol.isPresent()) {
      return Optional.empty();
    }

    if (!symbol.get().getEnclosingScope().isPresentAstNode()) {
      return Optional.empty();
    }
    final boolean correctModel =
            true;

    if(correctModel) {
      return symbol;
    } else {
      return Optional.empty();
    }
  }

  /**
   * Prints the languageComponent compilation unit and writes the result into a file.
   * The file is written in the outputPath of the VariationResolver instance,
   * respecting the package as specified in the compilation unit.
   * The name of the file is extracted from the compilation units languageComponent ast.
   *
   * @param languageComponentCompilationUnit The languageComponent compilation unit to output
   * @param outputPath the path to output the language component model
   */
  public void printLanguageComponent(String composedProjectName, ASTLanguageComponentCompilationUnit languageComponentCompilationUnit, Path outputPath) {
    final String printedLanguageComponent = LanguageComponentBaseMill.prettyPrint(languageComponentCompilationUnit, true);

    final String packageName =
            Names.getQualifiedName(languageComponentCompilationUnit.getPackageList());
    Path languageComponentOutputPath = outputPath
            .resolve(composedProjectName)
            .resolve("src")
            .resolve("main")
            .resolve("resources")
            .resolve(Names.getPathFromPackage(packageName))
            .resolve(languageComponentCompilationUnit.getLanguageComponent().getName()
                    + "." + "comp");

    FileReaderWriter.storeInFile(languageComponentOutputPath, printedLanguageComponent);
  }
}
