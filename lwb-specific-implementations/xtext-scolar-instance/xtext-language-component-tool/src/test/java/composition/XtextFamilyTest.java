package composition;

import com.google.inject.Injector;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.io.paths.MCPath;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._parser.LanguageComponentBaseParser;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily.LanguageFamilyResolver;
import languagefamily._symboltable.ILanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilyGlobalScope;
import languagefamily._symboltable.LanguageFamilySymbol;
import metacomposition.BaseLanguageComponentComposer;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.XtextStandaloneSetup;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.Binding;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class XtextFamilyTest {

    private XtextGrammarArtifactComposerHelper xtextGrammarHelper;
    Path modelPath = Paths.get("src\\test\\resources\\projects");
    Path famPath = Paths.get("src\\test\\resources\\");
    private XtextResourceSet resourceSet;
    private Injector injector;

    LanguageComponentBaseProcessor xtextLanguageComponentProcessor;
    MCPath mcPath;
    private LanguageFamilyProcessor familyTool;
    private LanguageFamilyResolver resolver;
    private Path outputPath = Paths.get("../usage/target/test-results/automataexample");


    @Before
    public void setUp()  {
        injector = new XtextStandaloneSetup().createInjectorAndDoEMFRegistration();
        resourceSet = injector.getInstance(org.eclipse.xtext.resource.XtextResourceSet.class);
        mcPath = new MCPath(modelPath, famPath);
        xtextLanguageComponentProcessor = new LanguageComponentBaseProcessor(mcPath);
        xtextGrammarHelper = new XtextGrammarArtifactComposerHelper(mcPath, resourceSet);
    }

    @Test
    public void testComponentComposition()  {
        Optional<LanguageComponentSymbol> sourceComponentSym = xtextLanguageComponentProcessor.loadLanguageComponentSymbol("character.CharExpression");
        Optional<LanguageComponentSymbol> targetComponentSym = xtextLanguageComponentProcessor.loadLanguageComponentSymbol("aut.Automaton");
        Optional<LanguageComponentSymbol> source2ComponentSym = xtextLanguageComponentProcessor.loadLanguageComponentSymbol("timed.TimedExpression");
        Assert.assertTrue(sourceComponentSym.isPresent());
        Assert.assertNotNull(sourceComponentSym.get().getAstNode());
        Assert.assertTrue(source2ComponentSym.isPresent());
        Assert.assertNotNull(source2ComponentSym.get().getAstNode());
        Assert.assertTrue(targetComponentSym.isPresent());
        Assert.assertNotNull(targetComponentSym.get().getAstNode());

        Collection<Binding> bindings = Arrays.asList(new Binding(Binding.BindingType.AS, "Char", "Guard"));
        Collection<Binding> bindings2 = Arrays.asList(new Binding(Binding.BindingType.AS, "Clock", "Guard"));;

        this.xtextGrammarHelper.compose(sourceComponentSym.get().getAstNode(), targetComponentSym.get().getAstNode(), bindings);

        Optional<Grammar> result = this.xtextGrammarHelper.getResult();

        Assert.assertTrue(result.isPresent());
//        xtextGrammarHelper.printGrammar(result.get());


        this.xtextGrammarHelper.compose(source2ComponentSym.get().getAstNode(), targetComponentSym.get().getAstNode(), bindings2);
        result = this.xtextGrammarHelper.getResult();

        Assert.assertTrue(result.isPresent());
//        xtextGrammarHelper.printGrammar(result.get());
    }

    @Test
    public void testLFComposition() throws IOException {
        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();

        familyTool = new LanguageFamilyProcessor(mcPath);
        resolver = constructResolver(mcPath, outputPath.resolve("output"));

        final String languageFamilyName = "AutomatonFamily";
        final String familyFQName = "fam." + languageFamilyName;
        final String featureConfigFileName = "src/test/resources/fam/Automaton.conf";
        Optional<ASTFCCompilationUnit> fc = new FeatureConfigurationParser().parse(featureConfigFileName);
        assertTrue("Could not load the feature configuration.", fc.isPresent());


        // Load the language family and variation interface configuration
        final Optional<LanguageFamilySymbol> family = familyTool.loadLanguageFamilySymbol(familyFQName);
        assertTrue("Could not load the initial language family.", family.isPresent());
        assertTrue("Symbol of initial LC has no AST node.", family.get().isPresentAstNode());


        final Optional<ASTLanguageComponentCompilationUnit> composedFamilyCompUnit = resolver
                .configureLanguageFamily(family.get().getAstNode(), fc.get().getFeatureConfiguration());

        ASTLanguageComponent test = composedFamilyCompUnit.get().getLanguageComponent();

        assertTrue("The process returned an empty result.", test != null);
    }




    private LanguageFamilyResolver constructResolver(MCPath modelPath, Path outputPath) throws IOException {
        AbstractArtifactComposer abstractArtifactComposer = new XtextArtifactComposer(modelPath, outputPath);

        ILanguageFamilyGlobalScope symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");
        LanguageComponentBaseProcessor lprocessor = new LanguageComponentBaseProcessor(symbolTable);

        BaseLanguageComponentComposer composer = new BaseLanguageComponentComposer(abstractArtifactComposer, lprocessor);

        return new LanguageFamilyResolver(composer, modelPath, outputPath);
    }

}
