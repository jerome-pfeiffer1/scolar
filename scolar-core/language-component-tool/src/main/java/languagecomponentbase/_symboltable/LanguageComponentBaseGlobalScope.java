package languagecomponentbase._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTool;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._parser.LanguageComponentBaseParser;
import languagecomponentbase._visitor.LanguageComponentBaseTraverser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LanguageComponentBaseGlobalScope extends LanguageComponentBaseGlobalScopeTOP {

    private LanguageComponentBaseParser parser;
    private LanguageComponentBaseScopesGenitorDelegator genitor;


    public LanguageComponentBaseGlobalScope(MCPath modelPath, String comp) {
        super(modelPath, comp);
        this.parser = new LanguageComponentBaseParser();
        genitor = LanguageComponentBaseMill.scopesGenitorDelegator();
    }

    public LanguageComponentBaseGlobalScope() {
        super();
    }

    @Override
    public LanguageComponentBaseGlobalScope getRealThis() {
        return this;
    }

    @Override
    public void loadFileForModelName(String modelName) {
        String ext = getFileExt();
        java.util.Optional<java.net.URL> location = getSymbolPath().find(modelName, ext);

        if(location.isPresent() && !isFileLoaded(location.get().toString())){
            addLoadedFile(location.get().toString());
            Optional<ASTLanguageComponentCompilationUnitA> astLanguageComponentCompilationUnitA = Optional.empty();
            Path p = null;
            try {
                p =  Paths.get(location.get().toURI());
                astLanguageComponentCompilationUnitA = parser.parse(p.toString());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            if(astLanguageComponentCompilationUnitA.isPresent()) {
                ILanguageComponentBaseArtifactScope as =
                        genitor.createFromAST(astLanguageComponentCompilationUnitA.get());
                if(astLanguageComponentCompilationUnitA.get().isPresentCDCompilationUnit()) {
                    as.setPackageName(Names.constructQualifiedName(astLanguageComponentCompilationUnitA.get().getCDCompilationUnit().getCDPackageList()));
                    // add imports
                    as.addAllImports(astLanguageComponentCompilationUnitA.get().getCDCompilationUnit()
                            .getMCImportStatementList().stream()
                            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                            .collect(Collectors.toList()));
                }
                else {
                    as.setPackageName(Names.constructQualifiedName(astLanguageComponentCompilationUnitA.get().getLanguageComponentCompilationUnit().getPackageList()));
                    // add imports
                    as.addAllImports(astLanguageComponentCompilationUnitA.get().getLanguageComponentCompilationUnit()
                            .getMCImportStatementList().stream()
                            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                            .collect(Collectors.toList()));
                }
//
                as.setEnclosingScope(this);
                addSubScope(as);
            }
        }
    }

}
