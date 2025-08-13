import aut._cocos.AutomatonWithCharExprCoCos;
import aut._generator.AutomatonGrammarWithCharacterExpressionGen;
import aut.automatongrammar.AutomatonGrammarMill;
import aut.automatongrammar._ast.ASTAutMain;
import aut.automatongrammarwithcharacterexpression._cocos.AutomatonGrammarWithCharacterExpressionCoCoChecker;
import aut.automatongrammarwithcharacterexpression._parser.AutomatonGrammarWithCharacterExpressionParser;
import characterexpression.characterexpression.CharacterExpressionMill;

import characterexpression.characterexpression._ast.ASTCharacterRule;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ParserTest {

    private static final String MODELPATH =
            "src" + File.separator + "test"
                    + File.separator + "resources"
                    + File.separator;

    @Before
    public void setup() {
        AutomatonGrammarMill.reset();
        CharacterExpressionMill.reset();
        AutomatonGrammarMill.init();
        CharacterExpressionMill.init();
        Log.enableFailQuick(false);
    }

    @Test
    public void testCIExampleCorrect() throws IOException {

        Files.deleteFiles(Paths.get("target/test-results/").toFile());

        AutomatonGrammarWithCharacterExpressionParser automatonGrammarWithCharacterExpressionParser =
                new AutomatonGrammarWithCharacterExpressionParser();

        Optional<ASTAutMain> parse =
                automatonGrammarWithCharacterExpressionParser.parse(
                        MODELPATH
                                +File.separator
                                + "TestAutWithCharExprCorrect.aut");
        assertTrue(parse.isPresent());

        AutomatonGrammarWithCharacterExpressionCoCoChecker checker =
                new AutomatonWithCharExprCoCos().createTransitionsCorrectChecker();

        checker.checkAll(parse.get());
        assertTrue(Log.getFindings().isEmpty());

        AutomatonGrammarWithCharacterExpressionGen generator = new AutomatonGrammarWithCharacterExpressionGen();
        generator.generate(parse.get(), Paths.get("target/test-results/"));
    }

    @Test
    public void testCIExampleIncorrectCoCo() throws IOException {
        AutomatonGrammarWithCharacterExpressionParser automatonGrammarWithCharacterExpressionParser = new AutomatonGrammarWithCharacterExpressionParser();

        Optional<ASTAutMain> parse =
                automatonGrammarWithCharacterExpressionParser.parse(
                        MODELPATH
                                +File.separator
                                + "IncorrectCoCo.aut");
        assertTrue(parse.isPresent());

        AutomatonGrammarWithCharacterExpressionCoCoChecker checker = new AutomatonWithCharExprCoCos().createTransitionsCorrectChecker();

        checker.checkAll(parse.get());
        assertFalse(Log.getFindings().isEmpty());
    }

    @Test
    public void testCIExampleInvalid() throws IOException {
        AutomatonGrammarWithCharacterExpressionParser automatonGrammarWithCharacterExpressionParser = new AutomatonGrammarWithCharacterExpressionParser();

        Optional<ASTAutMain> parse =
                automatonGrammarWithCharacterExpressionParser.parse(
                        MODELPATH
                                +File.separator
                                + "IncorrectLiteral.aut");
        assertFalse(parse.isPresent());
    }
}
