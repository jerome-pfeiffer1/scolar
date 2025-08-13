package asaggregater;

import aut.automata.AutomataMill;
import aut.automata._ast.ASTAutomaton;
import aut.automata._parser.AutomataParser;
import aut.automata._symboltable.IAutomataArtifactScope;
import aut.automata._symboltable.IStateSymbolResolver;
import aut.automata._symboltable.StateSymbol;
import aut.cd4a.CD4AMill;
import composition.MCArtifactComposer;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Files;
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
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the aggregation of language components. Tests a use case with all involved parts.
 *
 */
public class LanguageAggregationTest {

    private Path outputPath = Paths.get("target/test-results/");

    private MCPath MODEL_PATH = new MCPath(
            Paths.get("src/main/grammars"),
            Paths.get("src/main/resources/"),
            Paths.get("target/test-results/automataexample"));

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
     * Test that uses the Language Family to aggregate two Language Components (Automaton and CD4A). Aim is to generate the
     * infrastructure (Symbol Adapter and Symbol Resolver) which is needed for the composition.
     *
     * @throws IOException
     */
    @Test
    public void testAggregationWithLF() throws IOException {

        //FileUtils.cleanDirectory(Paths.get("target/test-results/automataexample").toFile());

        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();

        final String languageFamilyName = "AutomatonFamily";
        final String familyFQName = "aut." + languageFamilyName;
        final String featureConfigFileName = "src/main/resources/aut/AutomataWithCD4A.conf";

        final LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH);
        final LanguageFamilyResolver resolver = constructResolver(MODEL_PATH, outputPath.resolve("automataexample"));

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

    @Test
    public void testAggregationWithDummies() throws IOException {

        CD4AnalysisParser cdParser = new CD4AnalysisParser();
        AutomataParser autParser = new AutomataParser();

        String cdModel = "src/test/resources/test/CD4ATest.cd";
        ASTCDCompilationUnit cdAST = cdParser.parse(cdModel).get();
        CD4AnalysisMill.scopesGenitorDelegator().createFromAST(cdAST);

        String autModel = "src/test/resources/test/AutomatonTest.aut";
        ASTAutomaton autAST = autParser.parse(autModel).get();
        IAutomataArtifactScope autAS = AutomataMill.scopesGenitorDelegator().createFromAST(autAST);
        autAS.setName(autAST.toString());

        AutomataMill.globalScope().addAdaptedStateSymbolResolver(new CDType2StateResolver()); // uses dummy classes

        Optional<StateSymbol> s = AutomataMill.globalScope().resolveState("Bla");

        assertTrue(s.isPresent());
        assertEquals("Bla", s.get().getName());
        assertTrue(s.get() instanceof CDType2StateAdapter);
    }

    private LanguageFamilyResolver constructResolver(MCPath modelPath, Path outputPath) throws IOException {
        MCArtifactComposer abstractArtifactComposer = new MCArtifactComposer(modelPath, outputPath);

        ILanguageFamilyGlobalScope symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");
        MCLanguageComponentProcessor lprocessor = new MCLanguageComponentProcessor(symbolTable);

        BaseLanguageComponentComposer composer = new BaseLanguageComponentComposer(abstractArtifactComposer, lprocessor);

        return new LanguageFamilyResolver(composer, modelPath, outputPath);
    }

    // Inner classes for the generation of dummy symbol adapter and resolver
    private class CDType2StateResolver implements IStateSymbolResolver {

        @Override
        public List<StateSymbol> resolveAdaptedStateSymbol(boolean foundSymbols,
                                                           String name, AccessModifier modifier,
                                                           Predicate<StateSymbol> predicate) {
            List<StateSymbol> r = new ArrayList<>();
            Optional<CDTypeSymbol> s = CD4AnalysisMill
                    .globalScope()
                    .resolveCDType(name, modifier);

            if (s.isPresent()) {
                CDType2StateAdapter a = new CDType2StateAdapter(s.get());
                if (predicate.test(a)) {
                    r.add(a);
                }
            }
            return r;
        }
    }

    private class CDType2StateAdapter extends StateSymbol {

        private CDTypeSymbol original;
        public CDType2StateAdapter(CDTypeSymbol symbol) {
            super(symbol.getName());
            this.original = symbol;
        }

        @Override
        public String getName() {
            return original.getName();
        }

        public CDTypeSymbol getAdaptee() {
            return this.original;
        }
    }
}
