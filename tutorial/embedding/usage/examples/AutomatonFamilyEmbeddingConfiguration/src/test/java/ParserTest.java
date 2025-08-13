import aut._cocos.AutomatonWithCharExprWithTimedExpressionCoCos;
import aut._generator.AutomatonGrammarWithCharacterExpressionWithClockExpressionGen;
import aut.automatongrammar.AutomatonGrammarMill;
import aut.automatongrammar._ast.ASTAutMain;
import aut.automatongrammarwithcharacterexpressionwithclockexpression._cocos.AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker;
import aut.automatongrammarwithcharacterexpressionwithclockexpression._parser.AutomatonGrammarWithCharacterExpressionWithClockExpressionParser;
import characterexpression.characterexpression.CharacterExpressionMill;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import timedexpr.clockexpression.ClockExpressionMill;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

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
        ClockExpressionMill.reset();
        AutomatonGrammarMill.init();
        CharacterExpressionMill.init();
        ClockExpressionMill.init();
        Log.enableFailQuick(false);
    }

    @Test
    public void testCIExampleCorrect() throws IOException {

        Files.deleteFiles(Paths.get("target/test-results/").toFile());

        AutomatonGrammarWithCharacterExpressionWithClockExpressionParser automatonGrammarWithCharacterExpressionWithClockExpressionParser =
                new AutomatonGrammarWithCharacterExpressionWithClockExpressionParser();

        Optional<ASTAutMain> parse =
                automatonGrammarWithCharacterExpressionWithClockExpressionParser.parse(
                        MODELPATH
                                +File.separator
                                + "CorrectModel.aut");
        assertTrue(parse.isPresent());

        AutomatonGrammarWithCharacterExpressionWithClockExpressionCoCoChecker checker =
                new AutomatonWithCharExprWithTimedExpressionCoCos().createTransitionsCorrectChecker();

        checker.checkAll(parse.get());
        assertTrue(Log.getFindings().isEmpty());

        AutomatonGrammarWithCharacterExpressionWithClockExpressionGen generator =
                new AutomatonGrammarWithCharacterExpressionWithClockExpressionGen();
        generator.generate(parse.get(), Paths.get("target/test-results/"));
    }
}
