package languagecomponentbase.cocos;

import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTGrammarDefinition;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._cocos.LanguageComponentBaseASTLanguageComponentCoCo;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if language component contains a grammar more than one time.
 */
public class LCContainsGrammarMultipleTimes implements LanguageComponentBaseASTLanguageComponentCoCo {

    @Override
    public void check(ASTLanguageComponent node) {

        List<ASTGrammarDefinition> grammarDefinitions = node.getGrammarDefinitionList();
        List<String> grammarNames = new ArrayList<>();

        for (ASTGrammarDefinition grammarDefinition : grammarDefinitions) {
            String grammarName = grammarDefinition.getMCQualifiedName().toString();

            if (grammarNames.contains(grammarName)) {
                Log.error("LC022 Language Component contains Grammar Definition " + grammarName + " more than one time.");
            } else {
                grammarNames.add(grammarName);
            }

        }

    }
}
