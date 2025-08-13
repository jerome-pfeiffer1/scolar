package customizationconfiguration;

import java.util.Optional;

import customizationconfiguration._ast.ASTCCCompilationUnit;
import customizationconfiguration._ast.ASTCCompilationUnitA;
import customizationconfiguration._ast.ASTCustomizationConfigurationNode;
import customizationconfiguration._cocos.CustomizationConfigurationCoCoChecker;
import customizationconfiguration._symboltable.CustomizationConfigurationGlobalScope;
import customizationconfiguration._symboltable.CustomizationConfigurationSymbol;
import customizationconfiguration._symboltable.ICustomizationConfigurationGlobalScope;
import customizationconfiguration.cocos.CustomizationConfigurationCoCos;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;

public class CustomizationConfigurationProcessor {

  private final ICustomizationConfigurationGlobalScope symbolTable;

  public CustomizationConfigurationProcessor(
      MCPath modelPath) {
    this.symbolTable = new CustomizationConfigurationGlobalScope(modelPath, ".*");
  }

  public CustomizationConfigurationProcessor(ICustomizationConfigurationGlobalScope globalScope) {
    symbolTable = globalScope;
  }

  /**
   * Loads the customization configuration symbol of the customization
   * configuration model with the given qualified name that
   * should be located in one of the given model paths.
   * Checks the correctness using the standard CoCo-Checker for customization
   * configurations.
   *
   * @param qualifiedName Qualified name of the language component to load
   * @return Optional#empty, if not found or not correct.
   * The symbol of the model wrapped in an Optional, otherwise.
   */
  public Optional<CustomizationConfigurationSymbol> loadCCSymbol(
      String qualifiedName) {

    final Optional<CustomizationConfigurationSymbol> symbol =
        loadCCSymbolWithoutCoCos(qualifiedName);

    if (!symbol.isPresent()) {
      return Optional.empty();
    }

    if (!symbol.get().getEnclosingScope().isPresentAstNode()) {
      return Optional.empty();
    }
    ASTCCCompilationUnit ccCompilationUnit = ((ASTCCompilationUnitA) symbol.get().getEnclosingScope().getAstNode()).getCCCompilationUnit();

    final boolean correctModel = checkCoCos(ccCompilationUnit);

    if (correctModel) {
      return symbol;
    } else {
      return Optional.empty();
    }
  }

  /**
   * Loads the customization configuration symbol of the customization
   * configuration model with the given qualified name.
   *
   * @param qualifiedName Qualified name of the language component to load
   * @return Optional#empty, if not found.
   * The symbol of the model wrapped in an Optional, otherwise.
   */
  public Optional<CustomizationConfigurationSymbol> loadCCSymbolWithoutCoCos(
      String qualifiedName) {
    return symbolTable.resolveCustomizationConfiguration(qualifiedName);
  }

  /**
   * Applies the standard set of Context conditions to the given compilation
   * unit.
   *
   * @param node AST node to apply the CoCos to.
   * @return boolean returns true if every CoCo check succeed
   */
  public boolean checkCoCos(ASTCustomizationConfigurationNode node) {
    final CustomizationConfigurationCoCoChecker checker =
        CustomizationConfigurationCoCos.createChecker();

    final boolean failQuickEnabled = Log.isFailQuickEnabled();
    Log.enableFailQuick(false);

    checker.checkAll(node);

    if (Log.getErrorCount() != 0) {
      Log.debug(String.format(
          "Found %d errors in node %s.",
          Log.getErrorCount(), node.toString()), "XX");
      Log.enableFailQuick(failQuickEnabled);
      return false;
    }
    Log.enableFailQuick(failQuickEnabled);
    return true;
  }
}
