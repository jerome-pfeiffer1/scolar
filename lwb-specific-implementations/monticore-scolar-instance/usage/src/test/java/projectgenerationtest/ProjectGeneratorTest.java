package projectgenerationtest;

import aut.automata.AutomataMill;
import aut.cd4a.CD4AMill;
import composition.MCArtifactComposer;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.MCLanguageComponentProcessor;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily.LanguageFamilyResolver;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;
import metacomposition.BaseLanguageComponentComposer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Class for testing the project generation. It generates the full structure of the composed language project.
 * For testing a full input folder with two input project is provided in usage/src/test/resources/model.
 * One can save this folder outside this project-directory for a more realistic test (And changing the MODEL_PATH).
 */

public class ProjectGeneratorTest {

    private Path outputPath = Paths.get("../usage/target/test-results/automataexample");

    private MCPath MODEL_PATH = new MCPath(
            Paths.get("../usage/src/test/resources/model/automata-lang"),
            Paths.get("../usage/src/test/resources/model/automata-lang/src/main/grammars"),
            Paths.get("../usage/src/test/resources/model/automata-lang/src/main/resources"),
            Paths.get("../usage/src/test/resources/model/char-expr-lang"),
            Paths.get("../usage/src/test/resources/model/char-expr-lang/src/main/grammars"),
            Paths.get("../usage/src/test/resources/model/char-expr-lang/src/main/resources"),
            Paths.get("../usage/src/test/resources"));

    @Before
    public void setup(){
        AutomataMill.reset();
        CD4AMill.reset();
        CD4AnalysisMill.reset();
        AutomataMill.init();
        CD4AMill.init();
        CD4AnalysisMill.init();
        Log.enableFailQuick(false);
    }

    /**
     * Test for the generation of a composed language project (aggregation).
     * @throws IOException
     */

    @Test
    public void projectGenerationTest() throws IOException {

        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();

        final String languageFamilyName = "AutomatonFamily";
        final String familyFQName = "model." + languageFamilyName;
        final String featureConfigFileName = "../usage/src/test/resources/model/AutomataWithCharExpr.conf";

        final LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH);
        final LanguageFamilyResolver resolver = constructResolver(MODEL_PATH, outputPath.resolve("output"));

        // Load the language family and variation interface configuration
        final Optional<LanguageFamilySymbol> family = familyTool.loadLanguageFamilySymbol(familyFQName);
        assertTrue("Could not load the initial language family.", family.isPresent());
        assertTrue("Symbol of initial LC has no AST node.", family.get().isPresentAstNode());

        Optional<ASTFCCompilationUnit> fc = new FeatureConfigurationParser().parse(featureConfigFileName);
        assertTrue("Could not load the feature configuration.", fc.isPresent());

        final Optional<ASTLanguageComponentCompilationUnit> composedFamilyCompUnit = resolver
                .configureLanguageFamily(family.get().getAstNode(), fc.get().getFeatureConfiguration());

        ASTLanguageComponent test = composedFamilyCompUnit.get().getLanguageComponent();

        assertTrue("The process returned an empty result.", test != null);

    }

    private LanguageFamilyResolver constructResolver(MCPath modelPath, Path outputPath) throws IOException {
        MCArtifactComposer abstractArtifactComposer = new MCArtifactComposer(modelPath, outputPath);

        ILanguageFamilyGlobalScope symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");
        MCLanguageComponentProcessor lprocessor = new MCLanguageComponentProcessor(symbolTable);

        BaseLanguageComponentComposer composer = new BaseLanguageComponentComposer(abstractArtifactComposer, lprocessor);

        return new LanguageFamilyResolver(composer, modelPath, outputPath);
    }

}
