package composition;

import static junit.framework.TestCase.assertTrue;
import static util.Binding.BindingType.WFR;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase.MCLanguageComponentProcessor;
import languagecomponentbase._ast.ASTParameter;
import languagecomponentbase._symboltable.LanguageComponentSymbol;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._symboltable.ILanguageComponentBaseGlobalScope;
import languagecomponentbase._symboltable.LanguageComponentBaseGlobalScope;
import org.junit.Test;

import de.monticore.io.paths.MCPath;
import metacomposition.BaseLanguageComponentComposer;
import util.Binding;

public class WFRArtifactCompositionTest {

    public static final Path OUTPUT_PATH = Paths.get("target", "generated-test-models");
    public static final MCPath MODEL_PATH =
            new MCPath(Collections.singletonList(
                    Paths.get("target", "resources", "test", "sourcemodels")));


    @Test
    public void test() throws IOException {
        String epComponentName = "general.montiarcexample.montiarc.MontiArc";
        String ppComponentName = "general.montiarcexample.connectorCorrectness.ConnectorCorrectness";
        String statechartsName = "general.montiarcexample.statecharts.SC";
        String scCorrectName = "general.montiarcexample.statecharts.sccorrect.SCCorrect";

        MCArtifactComposer artifactComposer = new MCArtifactComposer(MODEL_PATH, OUTPUT_PATH);
        MCLanguageComponentProcessor lcProcessor =
                new MCLanguageComponentProcessor(MODEL_PATH);
        BaseLanguageComponentComposer composer =
                new BaseLanguageComponentComposer(
                        artifactComposer,
                        // TODO Integrate generator in test
                        //new NoOpInfrastructureGenerator(null, null, null),
                        lcProcessor);

        // Load test models
        ILanguageComponentBaseGlobalScope symboltable = new LanguageComponentBaseGlobalScope(MODEL_PATH, ".*");
        final Optional<LanguageComponentSymbol> montiArcSymbol = symboltable.resolveLanguageComponent(epComponentName);

        assertTrue(montiArcSymbol.isPresent());
        assertTrue(montiArcSymbol.get().getEnclosingScope().isPresentAstNode());

        final ASTLanguageComponentCompilationUnitA montiarcA = (ASTLanguageComponentCompilationUnitA) montiArcSymbol.get().getEnclosingScope().getAstNode();
        final ASTLanguageComponentCompilationUnit montiarc = montiarcA.getLanguageComponentCompilationUnit();
        final Optional<LanguageComponentSymbol> ccSymbol = symboltable.resolveLanguageComponent(ppComponentName);
        assertTrue(ccSymbol.isPresent());
        assertTrue(ccSymbol.get().getEnclosingScope().isPresentAstNode());

        final ASTLanguageComponentCompilationUnitA ccA = (ASTLanguageComponentCompilationUnitA) ccSymbol.get().getEnclosingScope().getAstNode();
        final ASTLanguageComponentCompilationUnit cc = ccA.getLanguageComponentCompilationUnit();
        final Optional<LanguageComponentSymbol> statechartsSymbol =
                symboltable.resolveLanguageComponent(statechartsName);
        assertTrue(statechartsSymbol.isPresent());
        assertTrue(statechartsSymbol.get().getEnclosingScope().isPresentAstNode());

        final ASTLanguageComponentCompilationUnitA statechartsA = (ASTLanguageComponentCompilationUnitA) statechartsSymbol.get().getEnclosingScope().getAstNode();
        final ASTLanguageComponentCompilationUnit statecharts = statechartsA.getLanguageComponentCompilationUnit();
        final Optional<LanguageComponentSymbol> sccorrectSymbol =
                symboltable.resolveLanguageComponent(scCorrectName);
        assertTrue(sccorrectSymbol.isPresent());
        assertTrue(sccorrectSymbol.get().getEnclosingScope().isPresentAstNode());
        final ASTLanguageComponentCompilationUnitA sccorrectA = (ASTLanguageComponentCompilationUnitA) sccorrectSymbol.get().getEnclosingScope().getAstNode();
        final ASTLanguageComponentCompilationUnit sccorrect = sccorrectA.getLanguageComponentCompilationUnit();

        // Composition

        List<Binding> bindings = new ArrayList<>();
        bindings.add(new Binding(WFR, "ConnectorCorrect", ""));

        final ASTLanguageComponentCompilationUnit maWithCc =
                composer.composeLanguageComponent(cc, montiarc, bindings);

        bindings = new ArrayList<>();
        bindings.add(new Binding(WFR, "TransitionsCorrect", "BasicSCCoCos"));
        bindings.add(new Binding(WFR, "BasicSCCorrectCoCos", ""));

        final ASTLanguageComponentCompilationUnit scWithScCorrect =
                composer.composeLanguageComponent(sccorrect, statecharts, bindings);

        bindings = new ArrayList<>();
        bindings.add(new Binding(WFR, "BasicSCCoCos", "BasicCoCos"));

        final ASTLanguageComponentCompilationUnit maa =
                composer.composeLanguageComponent(scWithScCorrect, maWithCc, bindings);

        final Optional<ASTParameter> booleanParameter = sccorrect.getLanguageComponent().getParameter("booleanParameter");
        assertTrue(booleanParameter.isPresent());
        artifactComposer.setParameter(null, booleanParameter.get(), "true");

        artifactComposer.outputResult("Project");
    }
}
