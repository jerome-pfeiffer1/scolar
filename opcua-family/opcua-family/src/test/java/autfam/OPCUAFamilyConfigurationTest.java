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
import languagefamily.LanguageFamilyProcessor;
import languagefamily.LanguageFamilyResolver;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;
import metacomposition.BaseLanguageComponentComposer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class OPCUAFamilyConfigurationTest {

    private Path outputPath = Paths.get("target/test-results/opcua-fam/");

    final MCPath MODEL_PATH = new MCPath(
            Paths.get("../opcua-base/src/main/grammars"),
            Paths.get("../opcua-base/src/main/resources"),
            Paths.get("../opcua-base"),
            Paths.get("../opcua-method/src/main/grammars"),
            Paths.get("../opcua-method/src/main/resources"),
            Paths.get("../opcua-method"),
            Paths.get("../opcua-enum/src/main/grammars"),
            Paths.get("../opcua-enum/src/main/resources"),
            Paths.get("../opcua-enum"),
            Paths.get("../opcua-struct/src/main/grammars"),
            Paths.get("../opcua-struct/src/main/resources"),
            Paths.get("../opcua-struct"),
            Paths.get("../opcua-family/src/main/resources")
            );

    @Before
    public void setUp() {
        Log.getFindings().clear();
        Log.enableFailQuick(false);

        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();
    }

    @Test
    public void testOPCUAFamilyConfig() throws IOException {

        Files.deleteFiles(outputPath.toFile());

        final String languageFamilyName = "OPCUAFamily";
        final String familyFQName = "opcfam." + languageFamilyName;
        final String featureConfigFileName = "src/test/resources/opcfam/OPCConfig.conf";

        final LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH);
        final LanguageFamilyResolver resolver =
                constructResolver(MODEL_PATH, outputPath);

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
