package aut;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagecomponentbase._symboltable.*;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class AutomataLanguageComponentTest {

    private Path outputPath = Paths.get("/target/test-results/");

    final MCPath MODEL_PATH = new MCPath(
            Paths.get("src/main/grammars"),
            Paths.get("src/main/resources"),
            Paths.get("target/generated-sources/monticore/sourcecode"));


    @Before
    public void setUp() {
        Log.getFindings().clear();
        Log.enableFailQuick(false);
    }

    @Test
    public void testAutomaton() {
        final String modelname = "aut.Automaton";
        final ASTLanguageComponent automaton =
                loadLanguageComponentAST(modelname);
        assertEquals(0, Log.getErrorCount());
    }

    protected ASTLanguageComponent loadLanguageComponentAST(String qualifiedName) {
        LanguageComponentBaseMill.reset();
        LanguageComponentBaseMill.init();
        final LanguageComponentBaseProcessor componentProcessor = new LanguageComponentBaseProcessor(MODEL_PATH);

        final Optional<LanguageComponentSymbol> languageComponentSymbol = componentProcessor.loadLanguageComponentSymbol(qualifiedName);

        assertTrue(languageComponentSymbol.isPresent());
        assertTrue(languageComponentSymbol.get().getAstNode().isPresentSymbol());
        assertTrue(languageComponentSymbol.get().getAstNode() instanceof ASTLanguageComponent);
        return languageComponentSymbol.get().getAstNode();
    }
}