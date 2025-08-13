/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase.cocos;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;


/**
 * Checks whether there is at least one provision point, extension point,
 * or well-formedness rule set in the language component.
 *
 * @author Michael Mutert
 */
public class AtLeastOnePPOrWfrSet
    implements LanguageComponentBaseASTLanguageComponentCoCo {
  
  @Override
  public void check(ASTLanguageComponent node) {

    final int wfrCount = node.getWfrSetDefinitions().size();
    final int numProvisionPoints = node.getProvisionPoints().size();

    if(wfrCount == 0 && numProvisionPoints == 0) {
      Log.error(
          "LC013 The language component " + node.getName()
              + " should at least define a single provision point or wfr set.",
          node.get_SourcePositionStart());
    }
  }
}
