/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.*;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;
import languagecomponentbase._symboltable.DomainModelDefinitionSymbol;
import languagecomponentbase._symboltable.LanguageComponentSymbol;

/**
 * Checks whether the referenced domain models exist.
 *
 * @author Jerome Pfeiffer
 */
public class ReferencedDomainModelReferencesExist implements LanguageComponentBaseASTLanguageComponentCoCo {

  @Override
  public void check(ASTLanguageComponent node) {
    for (ASTLanguageComponentElement element : node.getLanguageComponentElementList()) {
      List<String> namesToResolve = new ArrayList<>();
        if (element instanceof ASTProvidedGenExtension) {
          ((ASTProvidedGenExtension) element).getGeneratorRefList().forEach(ref -> namesToResolve.add(ref.getName()));
          ((ASTProvidedGenExtension) element).getProducerInterfaceRefList().forEach(ref -> namesToResolve.add(ref.getName()));
          ((ASTProvidedGenExtension) element).getProductInterfaceRefList().forEach(ref -> namesToResolve.add(ref.getName()));
        } else if (element instanceof ASTRequiredGenExtension) {
          ((ASTRequiredGenExtension) element).getGeneratorRefList().forEach(ref -> namesToResolve.add(ref.getName()));
          ((ASTRequiredGenExtension) element).getProducerInterfaceRefList().forEach(ref -> namesToResolve.add(ref.getName()));
          ((ASTRequiredGenExtension) element).getProductInterfaceRefList().forEach(ref -> namesToResolve.add(ref.getName()));
        }

        for (String name : namesToResolve) {
          LanguageComponentSymbol symbol = node.getSymbol();
          Optional<DomainModelDefinitionSymbol> domainModelDefinitionSymbol = symbol.getSpannedScope().resolveDomainModelDefinition(name);
          if (!domainModelDefinitionSymbol.isPresent()) {
            Log.error(String.format(
                            "LC011 Referenced domain model %s does not exist.",
                            name),
                    element.get_SourcePositionStart());
          }
        }
      }
  }

}
