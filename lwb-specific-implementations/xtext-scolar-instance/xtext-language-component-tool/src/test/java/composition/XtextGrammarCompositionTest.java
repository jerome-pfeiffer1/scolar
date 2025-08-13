package composition;

import com.google.inject.Injector;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import languagecomponentbase._ast.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.ISerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.Binding;

import java.io.*;
import java.util.*;

public class XtextGrammarCompositionTest {
    // 1. Set up Xtext and get the Injector
    Injector injector = null;

    // 2. Parse grammar file/string and get Grammar object
    XtextResourceSet resourceSet = null;

    private XtextGrammarArtifactComposerHelper xtextGrammarHelper = null;

    @Before
    public void setUp() {
        injector = new XtextStandaloneSetup().createInjectorAndDoEMFRegistration();
        resourceSet = injector.getInstance(org.eclipse.xtext.resource.XtextResourceSet.class);
        xtextGrammarHelper = new XtextGrammarArtifactComposerHelper(new MCPath("src/test/resources/grammars/"), resourceSet);
    }

    @Test
    public void testXtextGrammarParsing() {
        String grammarText =
                "grammar my.dsl.MyDsl with org.eclipse.xtext.CommonTerminals\n" +
                        "generate myDsl \"http://example.org/mydsl\"\n\n" +
                        "Model:\n" +
                        "    greetings+=Greeting*;\n\n" +
                        "Greeting:\n" +
                        "    'Hello' name=ID '!';";

        // Setup injector for parsing Xtext grammar itself
        Injector injector = new XtextStandaloneSetup().createInjectorAndDoEMFRegistration();

        // Get the parser for Xtext grammars
        IParser parser = injector.getInstance(IParser.class);

        // Parse the grammar text
        IParseResult result = parser.parse(new StringReader(grammarText));

        Grammar grammarAST = (Grammar) result.getRootASTElement();

        System.out.println("Grammar name: " + grammarAST.getName());
        grammarAST.getRules().forEach(rule -> System.out.println("Rule: " + rule.getName()));
    }

    @Test
    public void testXtextGrammarParsingAndPrintingFromFile() throws IOException {
        // 1. Set up Xtext and get the Injector
        Injector injector = new XtextStandaloneSetup().createInjectorAndDoEMFRegistration();

        // 2. Parse grammar file/string and get Grammar object
        XtextResourceSet resourceSet = injector.getInstance(org.eclipse.xtext.resource.XtextResourceSet.class);
        Resource res = resourceSet.createResource(org.eclipse.emf.common.util.URI.createURI("MyDSL.xtext"));
        res.load(new FileInputStream("src/test/resources/grammars/MyDSL.xtext"), null);

        Grammar grammar = (Grammar) res.getContents().get(0);
        System.out.println("Grammar name: " + grammar.getName());

        ResourceSet set = new ResourceSetImpl();

        Resource ecoreRes2 = set.getResource(URI.createFileURI("src/test/resources/ecore/CharExpression.ecore"), true);
        EPackage ePackage2 = (EPackage) ecoreRes2.getContents().get(0);
        EPackage.Registry.INSTANCE.put("http://www.CharExpression.charexpr", ePackage2);

//        // Fix ePackage references in grammar metamodel declarations
        for (AbstractMetamodelDeclaration decl : grammar.getMetamodelDeclarations()) {
            if (decl instanceof GeneratedMetamodel && ((GeneratedMetamodel) decl).getEPackage() == null) {
                GeneratedMetamodel gmm = (GeneratedMetamodel) decl;
                gmm.setEPackage(EPackage.Registry.INSTANCE.getEPackage(gmm.getEPackage().getNsURI()));
            } else if (decl instanceof ReferencedMetamodel && ((ReferencedMetamodel) decl).getEPackage() == null) {
                ReferencedMetamodel rmm = (ReferencedMetamodel) decl;
                rmm.setEPackage(EPackage.Registry.INSTANCE.getEPackage("http://www.CharExpression.charexpr"));
            }
        }

        
        ISerializer serializer = injector.getInstance(ISerializer.class);
        String serializedText = serializer.serialize(grammar);
        System.out.println(serializedText);
    }


    @Test
    public void assembleGrammarWithEcoreExtension() throws IOException {
        Injector injector = new XtextStandaloneSetup().createInjectorAndDoEMFRegistration();
        XtextFactory factory = XtextFactory.eINSTANCE;

        // Create a dummy EPackage + EClassifier to simulate an imported Ecore model
        EPackage dummyEPackage = EcoreFactory.eINSTANCE.createEPackage();
        dummyEPackage.setNsURI("http://www.TimedExpression.timedexpr");
        dummyEPackage.setName("timedExpression");
        dummyEPackage.setNsPrefix("timed");

        EClass clockExprClass = EcoreFactory.eINSTANCE.createEClass();
        clockExprClass.setName("ClockExpr");
        dummyEPackage.getEClassifiers().add(clockExprClass);
        EPackage.Registry.INSTANCE.put(dummyEPackage.getNsURI(), dummyEPackage);

        // Create the grammar
        Grammar grammar = factory.createGrammar();
        grammar.setName("my.GeneratedGrammar");

        // Create ReferencedMetamodel
        ReferencedMetamodel refMeta = factory.createReferencedMetamodel();
        refMeta.setAlias("timedExpression");
        refMeta.setEPackage(dummyEPackage);
        grammar.getMetamodelDeclarations().add(refMeta);

        // Create Rule: ClockExpr returns timedExpression::ClockExpr: 'early';
        ParserRule rule = factory.createParserRule();
        rule.setName("MyClockExpr"); // Different from ClockExpr (classifier)

        TypeRef typeRef = factory.createTypeRef();
        typeRef.setMetamodel(refMeta);
        typeRef.setClassifier(clockExprClass);
        rule.setType(typeRef);

        // Alternatives: just a keyword
        Keyword kw = factory.createKeyword();
        kw.setValue("early");
        rule.setAlternatives(kw);

        grammar.getRules().add(rule);

        // Put the grammar into a resource to enable proper scoping for serialization
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        Resource grammarResource = resourceSet.createResource(URI.createURI("dummy:/Generated.xtext"));
        grammarResource.getContents().add(grammar);

        // Serialize
        ISerializer serializer = injector.getInstance(ISerializer.class);
        String serialized = serializer.serialize(grammar);
        String patched = serialized.replaceAll("(\\w+)\\s+(\\w+)::(\\w+):", "$1 returns $2::$3:");

        System.out.println("Serialized Grammar:\n");
        System.out.println(patched);
    }

    @Test
    public void testGrammarCompositionWithThreeComponents() {
        List<String> targetRequiredExtensions = Arrays.asList("IGuardExpr");
        List<String> targetProvidedExtensions = Arrays.asList("AutMain");
        ASTLanguageComponent targetComponent = createLanguageComponent("Automaton", "AutomatonLang", targetRequiredExtensions, targetProvidedExtensions);

        List<String> sourceRequiredExtensions = Arrays.asList("ClockExpr");
        List<String> sourceProvidedExtensions = Arrays.asList("ClockExpr");
        ASTLanguageComponent sourceComponent = createLanguageComponent("Timer", "TimedExpression", sourceRequiredExtensions, sourceProvidedExtensions);

        Collection<Binding> bindings = new ArrayList<>();
        bindings.add(new Binding(Binding.BindingType.AS, "ClockExpr", "IGuardExpr"));

        this.xtextGrammarHelper.compose(sourceComponent, targetComponent, bindings);

        Optional<Grammar> result = this.xtextGrammarHelper.getResult();

        Assert.assertTrue(result.isPresent());
//        xtextGrammarHelper.printGrammar(result.get());

        List<String> source2RequiredExtensions = Arrays.asList("bub");
        List<String> source2ProvidedExtensions = Arrays.asList("CharacterRule");
        ASTLanguageComponent source2Component = createLanguageComponent("Character", "CharExpression", source2RequiredExtensions, source2ProvidedExtensions);

        Collection<Binding> bindings2 = Arrays.asList(new Binding(Binding.BindingType.AS, "CharacterRule", "IGuardExpr"));

        this.xtextGrammarHelper.compose(source2Component, targetComponent, bindings2);
        result = this.xtextGrammarHelper.getResult();

        Assert.assertTrue(result.isPresent());
//        xtextGrammarHelper.printGrammar(result.get());

    }

    private ASTLanguageComponent createLanguageComponent(String name, String grammarName, List<String> requiredExtensions, List<String> providedExtensions) {
        ASTLanguageComponent component = new ASTLanguageComponent();
        component.setName(name);
        ASTGrammarDefinition grammarDefinition = new ASTGrammarDefinitionBuilder().setMCQualifiedName(new ASTMCQualifiedNameBuilder().addParts(grammarName).build()).build();
        component.addGrammarDefinition(grammarDefinition);

        for (String requiredExtension : requiredExtensions) {
            ASTRequiredGrammarExtension requiredGrammarExtension = new ASTRequiredGrammarExtensionBuilder().setOptionality(ASTOptionality.OPTIONAL).setName(requiredExtension).build();
            component.addLanguageComponentElement(requiredGrammarExtension);
        }
        for (String providedExtension : providedExtensions) {
            ASTProvidedGrammarExtension providedGrammarExtension = new ASTProvidedGrammarExtensionBuilder().setName(providedExtension).build();
            component.addLanguageComponentElement(providedGrammarExtension);
        }

        return component;
    }


//    @Test
//    public void testXtextGrammarComposition() throws IOException {
//        Grammar automatonLang = loadGrammar("src/test/resources/grammars/AutomatonLang.xtext", "AutomatonLang");
//        Grammar timedExprLang = loadGrammar("src/test/resources/grammars/TimedExpression.xtext", "TimedExpression");
//
//        Assert.assertNotNull(automatonLang);
//        Assert.assertNotNull(timedExprLang);
//
//        // 1. Create new composed xtext grammar
//        Grammar composedGrammar = createNewGrammar(automatonLang.getName(), timedExprLang.getName());
//
//        // 2. Extend target grammar
//        composedGrammar.getUsedGrammars().add(automatonLang);
//
//
//        Map<AbstractRule, AbstractRule> originalRule2Copies = new HashMap<>();
//
//        // 3. Overwrite and add all rules of the target grammar and add to the composed grammar
//        for (AbstractRule originalRule : automatonLang.getRules()) {
//            ParserRule ruleOverwrite = createRuleOverwrite((ParserRule) originalRule);
//            originalRule2Copies.put(originalRule, ruleOverwrite);
//            composedGrammar.getRules().add(ruleOverwrite);
//        }
//        redirectRuleCallsToLocalCopies(composedGrammar, originalRule2Copies);
//
//        // 4. Import Ecore and add rules of source grammar.
//        addEcoreAndAddRules(timedExprLang, composedGrammar);
//
//
//        Resource grammarRes = resourceSet.createResource(URI.createURI(composedGrammar.getName() + ".xtext"));
//        grammarRes.getContents().add(composedGrammar);
//
//        // 5. Deserialize grammar
//        ISerializer serializer = injector.getInstance(ISerializer.class);
//        String result = serializer.serialize(composedGrammar);
//
//        System.out.println(formatReturnStatements(result));
//    }


//    @Test
//    public void testXtextRuleBinding() {
//        String sourceRule = "CharacterRule";
//        String sourceRule2 = "ClockExpr";
//        String targetRule = "IGuardExpr";
//
//        Grammar automatonLang = loadGrammar("src/test/resources/grammars/AutomatonLang.xtext", "AutomatonLang");
//        Grammar charExpressionLang = loadGrammar("src/test/resources/grammars/CharExpression.xtext", "CharExpression");
//        Grammar timedExpressionLang = loadGrammar("src/test/resources/grammars/TimedExpression.xtext", "TimedExpression");
//        Assert.assertNotNull(automatonLang);
//        Assert.assertNotNull(charExpressionLang);
//
//        Grammar result = composeGrammars(charExpressionLang, automatonLang, sourceRule, targetRule);
//        Resource grammarRes = resourceSet.createResource(URI.createURI(result.getName() + ".xtext"));
//        grammarRes.getContents().add(result);
//
//        // 5. Deserialize grammar
//        ISerializer serializer = injector.getInstance(ISerializer.class);
//        String printedResult = serializer.serialize(result);
//
//        System.out.println(formatReturnStatements(printedResult));
//
//        Assert.assertNotNull(printedResult);
//
//        Grammar result2 = composeGrammars(timedExpressionLang, result, sourceRule2, targetRule);
//        Resource grammarRes2 = resourceSet.createResource(URI.createURI(result2.getName() + ".xtext"));
//        grammarRes2.getContents().add(result2);
//
//        for (AbstractMetamodelDeclaration decl : result2.getMetamodelDeclarations()) {
//            if (decl instanceof GeneratedMetamodel && ((GeneratedMetamodel) decl).getEPackage() == null) {
//                GeneratedMetamodel gmm = (GeneratedMetamodel) decl;
//                gmm.setEPackage(EPackage.Registry.INSTANCE.getEPackage(gmm.getEPackage().getNsURI()));
//            }
//        }
//
//        String printedResult2 = serializer.serialize(result2);
//
//        System.out.println(formatReturnStatements(printedResult2));
//
//        Assert.assertNotNull(printedResult2);
//
//    }


}
