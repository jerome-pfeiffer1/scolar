import constraints._generator.ConstraintGenerator;
import constraints.constraint._ast.ASTConstraintDef;
import constraints.constraint._parser.ConstraintParser;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class ConstraintGenTest {

    @Ignore
    @Test
    public void testGenerate() throws IOException {
        ConstraintParser parser = new ConstraintParser();
        Optional<ASTConstraintDef> parse = parser.parse("src/test/resources/example.constraint");
        Assert.assertTrue(parse.isPresent());
        ASTConstraintDef astConstraintDef = parse.get();
        ConstraintGenerator generator = new ConstraintGenerator();
        generator.generateConstraintChecker(astConstraintDef, Paths.get("target/test-results"));
    }
}
