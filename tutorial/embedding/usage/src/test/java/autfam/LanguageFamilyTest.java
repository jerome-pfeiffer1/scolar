package autfam;

import composition.MCArtifactComposer;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.MCLanguageComponentProcessor;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyResolver;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._symboltable.*;
import metacomposition.BaseLanguageComponentComposer;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class LanguageFamilyTest {

    private Path rootPath = FileSystems.getDefault().getPath("..").toAbsolutePath();

    private Path projectPath = FileSystems.getDefault().getPath("").toAbsolutePath();

    final MCPath MODEL_PATH_EMBEDDING = new MCPath(
            Paths.get("../char-expr-lang/src/main/grammars"),
            Paths.get("../char-expr-lang/src/main/resources"),
            Paths.get("../char-expr-lang"),
            Paths.get("../automata-lang/src/main/grammars"),
            Paths.get("../automata-lang/src/main/resources"),
            Paths.get("../automata-lang"),
            Paths.get("../twitter-action-lang/src/main/grammars"),
            Paths.get("../twitter-action-lang/src/main/resources"),
            Paths.get("../twitter-action-lang/target/generated-sources/monticore/sourcecode"),
            Paths.get("../twitter-action-lang"),
            Paths.get("../timed-expr-lang/src/main/grammars"),
            Paths.get("../timed-expr-lang/src/main/resources"),
            Paths.get("../timed-expr-lang"),
            Paths.get("../automaton-family/src/main/resources"));


    @Before
    public void setUp() {
        Log.getFindings().clear();
        Log.enableFailQuick(false);

        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();
    }

    @Test
    public void testAutomatonFamily() {
        LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH_EMBEDDING);

        final Optional<LanguageFamilySymbol> languageFamilySymbol =
                familyTool.loadLanguageFamilySymbol("autfam.AutomatonFamilyEmbedding");

        assertTrue(languageFamilySymbol.isPresent());
        assertEquals(0, Log.getErrorCount());
    }

    @Test
    public void testLanguageFamilyEmbeddingConfiguration() throws IOException {

        Files.deleteFiles(Paths.get("target/test-results/automatonexample/embedding").toFile());

        final String languageFamilyName = "AutomatonFamilyEmbedding";
        final String familyFQName = "autfam." + languageFamilyName;
        final String featureConfigFileName = "src/main/resources/autfam/AutomatonFamilyEmbeddingConfiguration.conf";
        final Path fullOutputPath = Paths.get(projectPath.toString() ,"target/test-results/automatonexample/embedding");

        final LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH_EMBEDDING);
        final LanguageFamilyResolver resolver =
                constructResolver(MODEL_PATH_EMBEDDING, fullOutputPath);

        // Load the language family and variation interface configuration
        final Optional<LanguageFamilySymbol> family = familyTool.loadLanguageFamilySymbol(familyFQName);
        assertTrue("Could not load the initial language family.", family.isPresent());
        assertTrue("Symbol of initial LC has no AST node.", family.get().isPresentAstNode());

        Optional<ASTFCCompilationUnit> fc = new FeatureConfigurationParser().parse(featureConfigFileName);
        assertTrue("Could not load the feature configuration.", fc.isPresent());

        final Optional<ASTLanguageComponentCompilationUnit> composedFamilyCompUnit = resolver
                .configureLanguageFamily(
                        family.get().getAstNode(), fc.get().getFeatureConfiguration());

        ASTLanguageComponent test = composedFamilyCompUnit.get().getLanguageComponent();

        assertTrue("The process returned an empty result.", test != null);

        String prettyPrintPP = LanguageComponentBaseMill.prettyPrint(composedFamilyCompUnit.get(), true);
        System.out.println(prettyPrintPP);
    }

    private LanguageFamilyResolver constructResolver(MCPath modelPath, Path outputPath) throws IOException {
        MCArtifactComposer abstractArtifactComposer = new MCArtifactComposer(modelPath, outputPath);

        ILanguageFamilyGlobalScope symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");
        MCLanguageComponentProcessor lprocessor = new MCLanguageComponentProcessor(symbolTable);

        BaseLanguageComponentComposer composer = new BaseLanguageComponentComposer(abstractArtifactComposer, lprocessor);

        return new LanguageFamilyResolver(composer, modelPath, outputPath);
    }
}



