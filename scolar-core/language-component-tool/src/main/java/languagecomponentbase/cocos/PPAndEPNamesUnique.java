/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase.cocos;

import java.util.HashSet;
import java.util.Set;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.*;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

/**
 * Checks whether names of WFR sets, EPs and PPs are unique.
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public class PPAndEPNamesUnique implements LanguageComponentBaseASTLanguageComponentCoCo {
  
  @Override
  public void check(ASTLanguageComponent node) {
    Set<String> epNames = new HashSet<>();
    Set<String> ppNames = new HashSet<>();
    
    for (ASTLanguageComponentElement languageComponentElement : node.getLanguageComponentElementList()) {
      if (languageComponentElement instanceof ASTRequiredExtension) {
        final String epName = ((ASTRequiredExtension) languageComponentElement).getName();
        if (epNames.contains(epName)) {
          Log.error("LC001 Used ep name " + epName + " already exists.");
        }
        else {
          epNames.add(epName);
        }
      }
      
      if(languageComponentElement instanceof ASTProvidedExtension) {
        final String ppName = ((ASTProvidedExtension) languageComponentElement).getName();
        if(ppNames.contains(ppName)) {
          Log.error("LC002 Used pp name " + ppName + " already exists.");
        }
        else {
          ppNames.add(ppName);
        }
      }

      if(languageComponentElement instanceof ASTWfrSetDefinition) {
        final String wfrSetName = ((ASTWfrSetDefinition) languageComponentElement).getName();
        if(ppNames.contains(wfrSetName)) {
          Log.error("LC010 The name of the WFR set " + wfrSetName + " already exists.");
        }
        else {
          ppNames.add(wfrSetName);
        }
      }
    }
  }
}
