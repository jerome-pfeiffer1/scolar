package autfam;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._symboltable.LanguageFamilySymbol;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import util.Binding;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class AutomatonFamilyTest {
    private Path outputPath = Paths.get("/target/test-results/");

    final MCPath MODEL_PATH = new MCPath(
            Path.of("src/main/grammars"),
            Path.of("src/main/resources"),
            Path.of("target/generated-sources/monticore/sourcecode")
    );

    @Before
    public void setUp() {
        Log.getFindings().clear();
        Log.enableFailQuick(false);
    }

    @Test
    public void testAutomatonFamily() {
        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();
        LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH);

        final Optional<LanguageFamilySymbol> languageFamilySymbol =
                familyTool.loadLanguageFamilySymbol("autfam.AutomatonFamilyEmbedding");

        assertTrue(languageFamilySymbol.isPresent());
        assertEquals(0, Log.getErrorCount());
    }

}
