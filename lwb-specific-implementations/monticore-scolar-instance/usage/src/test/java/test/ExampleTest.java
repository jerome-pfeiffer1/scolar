package test;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import sc.StatechartWithFinalStateWithCharacterGen;
import sc.StatechartWithFinalStateWithCharacterWithCounterStatesGen;
import sc.statechart._ast.ASTSCMain;
import sc.statechartwithfinalstatewithcharacter._cocos.StatechartWithFinalStateWithCharacterCoCoChecker;
import sc.statechartwithfinalstatewithcharacter._parser.StatechartWithFinalStateWithCharacterParser;
import sc.statechartwithfinalstatewithcharacter.cocos.StatechartWithFinalStateWithCharGuardCoCos;
import sc.statechartwithfinalstatewithcharacterwithcounterstates._cocos.StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker;
import sc.statechartwithfinalstatewithcharacterwithcounterstates._parser.StatechartWithFinalStateWithCharacterWithCounterStatesParser;
import sc.statechartwithfinalstatewithcharacterwithcounterstates.cocos.StatechartWithFinalStateWithCharGuardWithCounterStateCoCos;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ExampleTest {

  @Before
  public void setUp() throws Exception {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
  }

  @Test
  public void name() throws IOException {
    StatechartWithFinalStateWithCharacterParser parser = new StatechartWithFinalStateWithCharacterParser();

    final Optional<ASTSCMain> parse = parser.parse("src/test/resources/test/TestSC.sc");
    assertTrue(parse.isPresent());

    StatechartWithFinalStateWithCharacterCoCoChecker checker = new StatechartWithFinalStateWithCharGuardCoCos().createInitialStateCoCosChecker();

    checker.checkAll(parse.get());
    assertTrue(Log.getFindings().isEmpty());

    StatechartWithFinalStateWithCharacterGen generator = new StatechartWithFinalStateWithCharacterGen();
    generator.generate(parse.get(), Paths.get("target/test-results/"));
  }

  @Test
  public void testWrong() throws IOException {
    StatechartWithFinalStateWithCharacterParser parser = new StatechartWithFinalStateWithCharacterParser();

    final Optional<ASTSCMain> parse = parser.parse("src/test/resources/test/TestSCWrong.sc");
    assertTrue(parse.isPresent());

    StatechartWithFinalStateWithCharacterCoCoChecker checker = new StatechartWithFinalStateWithCharGuardCoCos().createInitialStateCoCosChecker();

    checker.checkAll(parse.get());
    assertFalse(Log.getFindings().isEmpty());
  }

  @Test
  public void testCIExample() throws IOException {
    StatechartWithFinalStateWithCharacterWithCounterStatesParser parser = new StatechartWithFinalStateWithCharacterWithCounterStatesParser();

    final Optional<ASTSCMain> parse = parser.parse("src/test/resources/test/TestSCWithCounter.sc");
    assertTrue(parse.isPresent());

    StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker checker = new StatechartWithFinalStateWithCharGuardWithCounterStateCoCos().createInitialStateCoCosChecker();

    checker.checkAll(parse.get());
    assertTrue(Log.getFindings().isEmpty());

    StatechartWithFinalStateWithCharacterWithCounterStatesGen generator = new StatechartWithFinalStateWithCharacterWithCounterStatesGen();
    generator.generate(parse.get(), Paths.get("target/test-results/"));
  }

  @Test
  public void testCIExampleInvalid() throws IOException {
    StatechartWithFinalStateWithCharacterWithCounterStatesParser parser = new StatechartWithFinalStateWithCharacterWithCounterStatesParser();

    final Optional<ASTSCMain> parse = parser.parse("src/test/resources/test/TestSCWithCounterWrong.sc");
    assertTrue(parse.isPresent());

    StatechartWithFinalStateWithCharacterWithCounterStatesCoCoChecker checker = new StatechartWithFinalStateWithCharGuardWithCounterStateCoCos().createInitialStateCoCosChecker();

    checker.checkAll(parse.get());
    assertTrue(Log.getFindings().isEmpty());

  }
}
