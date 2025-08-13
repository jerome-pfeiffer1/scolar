/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase.cocos;


import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponentElement;
import languagecomponentbase._ast.ASTProvidedExtension;
import languagecomponentbase._ast.ASTRequiredExtension;
import languagecomponentbase._ast.ASTWfrSetDefinition;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentElementCoCo;

/**
 * WFR sets, EPs and PPs must start with an uppercase letter.
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public class PPAndEPStartUpperCase implements LanguageComponentBaseASTLanguageComponentElementCoCo {
  
  @Override
  public void check(ASTLanguageComponentElement node) {
    if (node instanceof ASTProvidedExtension) {
      String name = ((ASTProvidedExtension) node).getName();
      if (!Character.isUpperCase(name.charAt(0))) {
        Log.error("LC003 PP " + name + " starts with lower case. " +
            "It should start with an upper case letter.");
      }
    }
    
    if(node instanceof ASTRequiredExtension) {
      String name = ((ASTRequiredExtension) node).getName();
      if (!Character.isUpperCase(name.charAt(0))) {
        Log.error("LC004 EP " + name + " starts with lower case. " +
            "It should start with an upper case letter.");
      }
    }

    if(node instanceof ASTWfrSetDefinition) {
      String name = ((ASTWfrSetDefinition) node).getName();
      if (!Character.isUpperCase(name.charAt(0))) {
        Log.error("LC009 Name of the WFR set " + name + " starts with lower case. " +
            "It should start with an upper case letter.");
      }
    }
  }
  
}
