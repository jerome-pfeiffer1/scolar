/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagefamily._ast.ASTLanguageFamilyCompilationUnit;
import languagefamily._ast.ASTLanguageFamilyCompilationUnitA;
import languagefamily._ast.ASTLanguageFamilyNode;
import languagefamily._cocos.LanguageFamilyCoCoChecker;
import languagefamily._cocos.LanguageFamilyCoCos;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;

/**
 * Processor tool for the language family language
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public class LanguageFamilyProcessor {

  private final ILanguageFamilyGlobalScope symbolTable;


  /**
   *
   * @param symbolTable the symboltable to initialize the processor with.
   */
  public LanguageFamilyProcessor(ILanguageFamilyGlobalScope symbolTable) {
    this.symbolTable = symbolTable;
  }

  public LanguageFamilyProcessor(MCPath modelPath) {
    symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");
  }


  /**
   * Load the symbol for the language family with the given fully qualified name from
   * the given symbol table + performs coco check.
   *
   * @param qualifiedName Name of the language family to load
   * @param symbolTable Symbol table to load the language family from
   * @return Optional containing the symbol of the LC if found and correct,
   * {@link Optional#empty()} otherwise.
   */
  public Optional<LanguageFamilySymbol> loadLanguageFamilySymbol(
          String qualifiedName,
          ILanguageFamilyGlobalScope symbolTable) {

    final Optional<LanguageFamilySymbol> symbol =
            loadLanguageFamilySymbolWithoutCoCos(qualifiedName);

    if (!symbol.isPresent()) {
      return Optional.empty();
    }

    if (!symbol.get().getEnclosingScope().isPresentAstNode()) {
      return Optional.empty();
    }
    final boolean correctModel =
        checkCoCos(((ASTLanguageFamilyCompilationUnitA) symbol.get().getEnclosingScope().getAstNode()).getLanguageFamilyCompilationUnit());

    if(correctModel) {
      return symbol;
    } else {
      return Optional.empty();
    }
  }

  public Optional<LanguageFamilySymbol> loadLanguageFamilySymbolWithoutCoCos(String qualifiedName) {
    return symbolTable.resolveLanguageFamily(qualifiedName);
  }

  public Optional<ASTLanguageFamilyCompilationUnit> loadCompilationUnit(String qualifiedName) {
    final Optional<LanguageFamilySymbol> languageFamilySymbol =
        loadLanguageFamilySymbol(qualifiedName);
    if(!languageFamilySymbol.isPresent()) {
      return Optional.empty();
    }

    final Optional<ASTNode> spanningNode = Optional.ofNullable(languageFamilySymbol.get().
            getEnclosingScope().getAstNode());

    if(!spanningNode.isPresent()) {
      return Optional.empty();
    }
    ASTLanguageFamilyCompilationUnit languageFamilyCompilationUnit = ((ASTLanguageFamilyCompilationUnitA) spanningNode.get()).getLanguageFamilyCompilationUnit();
    return Optional.of(languageFamilyCompilationUnit);
  }

  /**
   * Load the symbol for the language family with the given fully qualified name.
   *
   * @param qualifiedName Name of the language family to load
   * @return Optional containing the symbol of the LC if found and correct,
   * {@link Optional#empty()} otherwise.
   */
  public Optional<LanguageFamilySymbol> loadLanguageFamilySymbol(String qualifiedName){
    return loadLanguageFamilySymbol(qualifiedName, this.symbolTable);
  }


  /**
   * Applies the standard set of Context conditions to the given compilation
   * unit.
   *
   * @param node AST node to apply the CoCos to.
   * @return true if all cocos are met.
   */
  public static boolean checkCoCos(ASTLanguageFamilyNode node) {
    final LanguageFamilyCoCoChecker checker =
        LanguageFamilyCoCos.createChecker();

    final boolean failQuickEnabled = Log.isFailQuickEnabled();
    Log.enableFailQuick(false);

    //TODO
//    checker.checkAll(node);

    if (Log.getErrorCount() != 0) {
      Log.debug(String.format(
          "Found %d errors in node %s.",
          Log.getErrorCount(), node.toString()), "LanguageFamilyProcessor");
      Log.getFindings().clear();
      Log.enableFailQuick(failQuickEnabled);
      return false;
    }
    Log.enableFailQuick(failQuickEnabled);
    return true;
  }
}
