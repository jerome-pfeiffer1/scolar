/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package composition;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.java.javadsl._ast.*;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._symboltable.*;
import util.Binding;
import util.Binding.BindingType;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.javalight._ast.ASTClassBodyDeclaration;

/**
 * TODO: Write me!
 *
 * @author Jerome Pfeiffer
 * @author Michael Mutert
 */
public class GENCompositionTest {

    protected static final Path OUTPUT_PATH = Paths.get("target", "generated-test-models");
    protected static final MCPath MODEL_PATH = new MCPath(Paths.get("src", "test", "resources"));

    @Before
    public void setup() {
        LanguageComponentBaseMill.reset();
        LanguageComponentBaseMill.init();
        //Log.enableFailQuick(true);
        //Log.enableFailQuick(true);
    }
    @Test
    public void testCD4CodeParser() {
        CD4CodeParser parser = new CD4CodeParser();

        List<Path> cdPathList = new ArrayList<>();
        cdPathList.add(
                Paths.get("src", "test", "resources", "gencomposition", "domainmodel", "montiarc", "MA2JavaProducer.cd"));
        cdPathList.add(
                Paths.get("src", "test", "resources", "gencomposition", "domainmodel", "montiarc", "MA2JavaProduct.cd"));
        cdPathList.add(
                Paths.get("src", "test", "resources", "gencomposition", "domainmodel", "statechart", "SC2JavaProducer.cd"));
        cdPathList.add(
                Paths.get("src", "test", "resources", "gencomposition", "domainmodel", "statechart", "SC2JavaProduct.cd"));

        for (Path path : cdPathList) {
            Optional<ASTCDCompilationUnit> parse = Optional.empty();
            try {
                parse = parser.parse(path.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertTrue(parse.isPresent());
        }
    }

    @Test
    public void testCDTypeResolving() {
        LanguageComponentBaseProcessor languageComponentBaseProcessor = new LanguageComponentBaseProcessor(MODEL_PATH);
        Optional<LanguageComponentSymbol> scComponent = languageComponentBaseProcessor.loadLanguageComponentSymbol(
                "gencomposition.SC");
        assertTrue(scComponent.isPresent());

        Optional<LanguageComponentSymbol> maComponent = languageComponentBaseProcessor.loadLanguageComponentSymbol(
                "gencomposition.MontiArc");
        assertTrue(maComponent.isPresent());

        ILanguageComponentBaseScope scSpannedScope = scComponent.get().getSpannedScope();
        Optional<DomainModelDefinitionSymbol> sc2JavaSymbol = scSpannedScope.resolveDomainModelDefinition("SC2Java");
        assertTrue(sc2JavaSymbol.isPresent());

        ILanguageComponentBaseScope maSpannedScope = maComponent.get().getSpannedScope();
        Optional<DomainModelDefinitionSymbol> iMontiArcGenerator = maSpannedScope.resolveDomainModelDefinition("IMontiArcGenerator");
        assertTrue(iMontiArcGenerator.isPresent());

        Optional<DomainModelDefinitionSymbol> ma2Java = maSpannedScope.resolveDomainModelDefinition("MA2Java");
        assertTrue(ma2Java.isPresent());
    }

    @Test
    public void testRegisterGeneration() throws IOException {
        ILanguageComponentBaseGlobalScope symboltable = new LanguageComponentBaseGlobalScope(MODEL_PATH, ".*");
        Optional<LanguageComponentSymbol> scComponent = symboltable.resolveLanguageComponent("gencomposition.SC");

        assertTrue(scComponent.isPresent());
        assertTrue(scComponent.get().getEnclosingScope().isPresentAstNode());

        Optional<LanguageComponentSymbol> maComponent = symboltable.resolveLanguageComponent(
                "gencomposition.MontiArc");
        assertTrue(maComponent.isPresent());
        assertTrue(maComponent.get().getEnclosingScope().isPresentAstNode());

        Optional<LanguageComponentSymbol> varComponent = symboltable.resolveLanguageComponent(
                "gencomposition.Variable");
        assertTrue(varComponent.isPresent());
        assertTrue(varComponent.get().getEnclosingScope().isPresentAstNode());

        Binding b1 = new Binding(BindingType.GEN, "Variable2Java", "Element2Java");
        Binding b2 = new Binding(Binding.BindingType.GEN, "Main2Java", "Verhalten2Java");

        MCGeneratorArtifactComposerHelper helper = new MCGeneratorArtifactComposerHelper(MODEL_PATH);

        ASTLanguageComponentCompilationUnit varComponentCompilationUnit = ( (ASTLanguageComponentCompilationUnitA) varComponent.get().getEnclosingScope().getAstNode()).getLanguageComponentCompilationUnit();
        ASTLanguageComponentCompilationUnit scComponentCompilationUnit = ( (ASTLanguageComponentCompilationUnitA) scComponent.get().getEnclosingScope().getAstNode()).getLanguageComponentCompilationUnit();
        ASTLanguageComponentCompilationUnit maComponentCompilationUnit = ( (ASTLanguageComponentCompilationUnitA) maComponent.get().getEnclosingScope().getAstNode()).getLanguageComponentCompilationUnit();

        helper.compose("Bubu", "mc.lang.statechart", "Bubu", b1, varComponentCompilationUnit,
                scComponentCompilationUnit);
        helper.compose("Bubu", "mc.lang.montiarc", "Bubu", b2, scComponentCompilationUnit,
                maComponentCompilationUnit);

        helper.outputResult(OUTPUT_PATH, "Hello" , "a.b", "");
    }

    @Test
    public void testAdapterGeneration() throws IOException {
        LanguageComponentBaseProcessor languageComponentBaseProcessor = new LanguageComponentBaseProcessor(MODEL_PATH);
        Optional<LanguageComponentSymbol> scComponent = languageComponentBaseProcessor.loadLanguageComponentSymbol(
                "gencomposition.SC");
        assertTrue(scComponent.isPresent());
        assertTrue(scComponent.get().isPresentAstNode());

        Optional<LanguageComponentSymbol> maComponent = languageComponentBaseProcessor.loadLanguageComponentSymbol(
                "gencomposition.MontiArc");
        assertTrue(maComponent.isPresent());
        assertTrue(maComponent.get().isPresentAstNode());

        Binding binding = new Binding(Binding.BindingType.GEN, "Main2Java", "Verhalten2Java");

        MCGeneratorArtifactComposerHelper helper = new MCGeneratorArtifactComposerHelper(MODEL_PATH);
        ASTLanguageComponentCompilationUnit scComponentCompUnitAST = ( (ASTLanguageComponentCompilationUnitA) scComponent.get().getEnclosingScope().getAstNode()).getLanguageComponentCompilationUnit();
        ASTLanguageComponentCompilationUnit maComponentCompUnitAST = ( (ASTLanguageComponentCompilationUnitA) maComponent.get().getEnclosingScope().getAstNode()).getLanguageComponentCompilationUnit();

        Optional<String> producerAdapter = helper.generateProducerInterfaceAdapter("ComposedProject",
                "gencomposition",
                binding,
                scComponentCompUnitAST,
                maComponentCompUnitAST,
                OUTPUT_PATH);

        Optional<String> productAdapter = helper.generateProductInterfaceAdapter("ComposedProject",
                "gencomposition",
                binding,
                scComponentCompUnitAST,
                maComponentCompUnitAST,
                OUTPUT_PATH);

        assertTrue(productAdapter.isPresent());
        assertTrue(producerAdapter.isPresent());

        System.out.println(productAdapter);
        System.out.println(producerAdapter);

        JavaDSLParser javaParser = new JavaDSLParser();
        Optional<ASTCompilationUnit> parsedProducer = javaParser.parse_String(producerAdapter.get());
        Optional<ASTCompilationUnit> parsedProduct = javaParser.parse_String(productAdapter.get());

        assertTrue(parsedProducer.isPresent());
        assertTrue(parsedProduct.isPresent());

        ASTClassDeclaration producerClassDef = (ASTClassDeclaration)((ASTOrdinaryCompilationUnit) parsedProducer.get()).getTypeDeclaration(0);
        checkAdapterClass(producerClassDef, "ISCGen2IBehaviorGenTOP", "IBehaviorGen", "ISCGen", false);

        ASTClassDeclaration productClassDef = (ASTClassDeclaration)((ASTOrdinaryCompilationUnit) parsedProduct.get()).getTypeDeclaration(0);
        checkAdapterClass(
                productClassDef, "ISCRTEBehavior2IComputableTOP",
                "IComputable", "ISCRTEBehavior", true);

    }
    private void checkAdapterClass(ASTClassDeclaration classDef, String className,
                                   String interfaceName, String delegateTypeName, boolean isProductAdapter) {
        assertEquals(className, classDef.getName());

        ASTMCType implementedInterface = classDef.getImplementedInterface(0);
        String implementedInterfaceName = ((ASTMCQualifiedType)implementedInterface).getAnnotatedName(0).getName();
        assertEquals(interfaceName, implementedInterfaceName);

        Optional<ASTClassBodyDeclaration> classAttr = classDef.getClassBody()
                .getClassBodyDeclarationList().stream().filter(d -> d instanceof ASTFieldDeclaration)
                .findAny();
        assertTrue(classAttr.isPresent());

        assertEquals(delegateTypeName, ((ASTMCQualifiedType)((ASTFieldDeclaration)classAttr.get()).getMCType()).getAnnotatedName(0).getName());
        if (isProductAdapter) {
            assertEquals(2, classDef.getClassBody().getClassBodyDeclarationList().size());
        }
        else {
            assertEquals(3, classDef.getClassBody().getClassBodyDeclarationList().size());
        }

    }
}
