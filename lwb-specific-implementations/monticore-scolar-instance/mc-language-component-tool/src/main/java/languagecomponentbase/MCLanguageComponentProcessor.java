/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._symboltable.ILanguageComponentBaseGlobalScope;
import languagecomponentbase._ast.ASTLanguageComponentBaseNode;
import languagecomponentbase._cocos.LanguageComponentBaseCoCoChecker;
import languagecomponentbase.cocos.LanguageComponentBaseCoCos;

import java.nio.file.Path;

public class MCLanguageComponentProcessor extends LanguageComponentBaseProcessor {

  /**
   * Constructor for languagecomponentbase.cocos.MCLanguageComponentProcessor
   * @param modelPath
   */
  public MCLanguageComponentProcessor(MCPath modelPath) {
    super(modelPath);
  }

  public MCLanguageComponentProcessor(ILanguageComponentBaseGlobalScope globalScope) {
    super(globalScope);
  }
  
  
  /**
   * //@see languagecomponentbase.LanguageComponentBaseProcessor#checkCoCos(languagecomponentbase._ast.ASTLanguageComponentBaseNode)
   */
  //@Override
  public boolean checkCoCos(ASTLanguageComponentBaseNode node) {
    final LanguageComponentBaseCoCoChecker checker = LanguageComponentBaseCoCos.createChecker();
    final boolean failQuickEnabled = Log.isFailQuickEnabled();
    Log.enableFailQuick(false);
    
    checker.checkAll(node);
    
    if (Log.getErrorCount() != 0) {
      Log.debug(String.format(
          "Found %d errors in node %s.",
          Log.getErrorCount(), node.toString()), "XX");
      Log.getFindings().clear();
      Log.enableFailQuick(failQuickEnabled);
      return false;
    }
    Log.enableFailQuick(failQuickEnabled);
    return true;
  }
}
