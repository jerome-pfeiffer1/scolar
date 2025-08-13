package composition;

import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import languagecomponentbase._ast.*;
import org.junit.Test;
import util.Binding;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class XtextWFRCompositionTest {

    @Test
    public void testWFRCompositionSimple() throws IOException {
        List<String> peWfrList = Arrays.asList("AtLeastOneFinalState", "StateNameIsUpperCase");
        String wfrSetName = "StateCorrect";
        String peComponentName = "FinalState";
        String peGrammarName = "FSG";
        String peRuleName = "FS";
        ASTLanguageComponentCompilationUnit fSComponent = createLanguageComponent(peComponentName, peGrammarName, wfrSetName, peWfrList, peRuleName);

        List<String> ppWfrList = Arrays.asList("AutomatonIsComplete");
        String reComponentName = "AutomatonComponent";
        String reGrammarName = "Automaton";
        String reWfrSetName = "AutomatonCorrect";
        String reRuleName = "AutMain";
        ASTLanguageComponentCompilationUnit autComponent = createLanguageComponent(reComponentName, reGrammarName, reWfrSetName, ppWfrList, reRuleName);

        Binding b = new Binding(Binding.BindingType.WFR, wfrSetName, reWfrSetName);

        Path outputPath = Paths.get("target/test-results/wfrComposition");
        XtextArtifactComposer composer = new XtextArtifactComposer(new MCPath(), outputPath);
        composer.compose(fSComponent, autComponent, Arrays.asList(b));
//        composer.outputResult();
    }

    private ASTLanguageComponentCompilationUnit createLanguageComponent(String name, String grammarName, String wfrSetName, List<String> wfrList, String ruleName) {

        ASTLanguageComponentBuilder componentBuilder = new ASTLanguageComponentBuilder();
        componentBuilder.setName(name);
        ASTGrammarDefinition grammarDefinition = new ASTGrammarDefinitionBuilder().setMCQualifiedName
                (new ASTMCQualifiedNameBuilder().addParts(grammarName).build()).build();
        componentBuilder.addGrammarDefinition(grammarDefinition);
        ASTWfrSetDefinitionBuilder wfrSetBuilder = new ASTWfrSetDefinitionBuilder();
        wfrSetBuilder.setName(wfrSetName);
        wfrSetBuilder.setReferencedRule(ruleName);
        ASTDefaultWfrDefinitionBuilder wfrDefinitionBuilder = new ASTDefaultWfrDefinitionBuilder();
        for (String wfr : wfrList) {
            ASTMCQualifiedNameBuilder mcQualifiedNameBuilder = new ASTMCQualifiedNameBuilder();
            mcQualifiedNameBuilder.addParts(wfr);
            wfrDefinitionBuilder.setWfrReference(mcQualifiedNameBuilder.build());
        }
        wfrSetBuilder.addWfrDefinition(wfrDefinitionBuilder.build());

        ASTWfrSetDefinition wfrSetDefinition = wfrSetBuilder.build();
        componentBuilder.addLanguageComponentElement(wfrSetDefinition);
        ASTLanguageComponent component = componentBuilder.build();

        ASTLanguageComponentCompilationUnitBuilder builder = new ASTLanguageComponentCompilationUnitBuilder();
        builder.setLanguageComponent(component);
        builder.setPackageList(Arrays.asList("a.b"));
        return builder.build();
    }
}
