import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTool;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagecomponentbase.LanguageComponentBaseTool;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._parser.LanguageComponentBaseParser;
import languagecomponentbase._symboltable.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolTableTest {

    private final MCPath modelPath = new MCPath(Paths.get("src/test/resources/sourcemodels/"));

    @Before
    public void setup() {

        LanguageComponentBaseMill.reset();
        LanguageComponentBaseMill.init();

        Log.enableFailQuick(false);
    }

    @Test
    public void testSymbolTable() {
        LanguageComponentBaseParser parser = new LanguageComponentBaseParser();

        Optional<ASTLanguageComponentCompilationUnitA> montiArcLanguageComponent = null;
        try {
            montiArcLanguageComponent = parser
                    .parse(modelPath.getEntries().toArray()[0].toString() + File.separator +  "general" + File.separator + "montiarcexample" + File.separator + "statecharts" + File.separator + "SC.comp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(montiArcLanguageComponent.isPresent());

        LanguageComponentBaseTool languageComponentBaseTool = new LanguageComponentBaseTool();
        ILanguageComponentBaseArtifactScope symbolTable = languageComponentBaseTool.createSymbolTable(montiArcLanguageComponent.get());
        ILanguageComponentBaseScope iLanguageComponentBaseScope = symbolTable.getSubScopes().stream().findAny().get();
        String name = symbolTable.getName();
        System.out.println(name);
        LinkedListMultimap<String, ProvidedGenExtensionSymbol> providedGenExtensionSymbols = iLanguageComponentBaseScope.getProvidedGenExtensionSymbols();
        LinkedListMultimap<String, RequiredGenExtensionSymbol> requiredGenExtensionSymbols = iLanguageComponentBaseScope.getRequiredGenExtensionSymbols();
        LinkedListMultimap<String, RequiredGrammarExtensionSymbol> requiredGrammarExtensionSymbols = iLanguageComponentBaseScope.getRequiredGrammarExtensionSymbols();
        LinkedListMultimap<String, ProvidedGrammarExtensionSymbol> providedGrammarExtensionSymbols = iLanguageComponentBaseScope.getProvidedGrammarExtensionSymbols();

        Optional<RequiredGenExtensionSymbol> scElementTrafo = iLanguageComponentBaseScope.resolveRequiredGenExtension("SCElementTrafo");
        assertTrue(scElementTrafo.isPresent());
        assertEquals(1, providedGenExtensionSymbols.size());
        assertEquals(1, requiredGenExtensionSymbols.size());
        assertEquals(2, providedGrammarExtensionSymbols.size());
        assertEquals(1, requiredGrammarExtensionSymbols.size());

        assertEquals(1, iLanguageComponentBaseScope.getParameterSymbols().size());
    }

    @Test
    public void testLanguageComponentBaseProcessor() {
        LanguageComponentBaseProcessor proc = new LanguageComponentBaseProcessor(modelPath);
        Optional<LanguageComponentSymbol> languageComponentSymbol = proc.loadLanguageComponentSymbolWithoutCoCos("general.montiarcexample.statecharts.SC");
        assertTrue(languageComponentSymbol.isPresent());

        Optional<DomainModelDefinitionSymbol> sc2Java = languageComponentSymbol.get().getSpannedScope().resolveDomainModelDefinition("ISCGen");
        assertTrue(sc2Java.isPresent());
        assertTrue(sc2Java.get() instanceof CDTypeSymbol2DomainModelDefinitionAdapter);

    }
}
