package languagefamily._symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._parser.LanguageComponentBaseParser;
import languagecomponentbase._symboltable.ILanguageComponentBaseArtifactScope;
import languagecomponentbase._symboltable.LanguageComponentBaseScopesGenitorDelegator;
import languagefamily.LanguageFamilyMill;
import languagefamily._ast.ASTLanguageFamilyCompilationUnitA;
import languagefamily._parser.LanguageFamilyParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageFamilyGlobalScope extends LanguageFamilyGlobalScopeTOP {

    private LanguageFamilyParser parser;
    private LanguageFamilyScopesGenitorDelegator genitor;

    @Override
    public LanguageFamilyGlobalScope getRealThis() {
        return this;
    }

    public LanguageFamilyGlobalScope() {
        super();
    }

    public LanguageFamilyGlobalScope(MCPath modelPath, String fileExt) {
        super(modelPath, fileExt);
        this.parser = new LanguageFamilyParser();
        genitor = LanguageFamilyMill.scopesGenitorDelegator();
    }

    @Override
    public void loadFileForModelName(String modelName) {
        String ext = getFileExt();
        java.util.Optional<java.net.URL> location = getSymbolPath().find(modelName, ext);

        if(location.isPresent() && !isFileLoaded(location.get().toString())){
            addLoadedFile(location.get().toString());
            Optional<ASTLanguageFamilyCompilationUnitA> astLanguageFamilyCompilationUnitA = Optional.empty();
            Path p = null;
            try {
                p =  Paths.get(location.get().toURI());
                astLanguageFamilyCompilationUnitA = parser.parse(p.toString());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            if(astLanguageFamilyCompilationUnitA.isPresent()) {
                ILanguageFamilyArtifactScope as =
                        genitor.createFromAST(astLanguageFamilyCompilationUnitA.get());
                if(astLanguageFamilyCompilationUnitA.get().isPresentLanguageComponentCompilationUnitA()) {
                    if (astLanguageFamilyCompilationUnitA.get().getLanguageComponentCompilationUnitA().isPresentCDCompilationUnit()) {
                        as.setPackageName(Names.constructQualifiedName(astLanguageFamilyCompilationUnitA.get().getLanguageComponentCompilationUnitA().getCDCompilationUnit().getCDPackageList()));
                        // add imports
                        as.addAllImports(astLanguageFamilyCompilationUnitA.get().getLanguageComponentCompilationUnitA().getCDCompilationUnit()
                                .getMCImportStatementList().stream()
                                .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                                .collect(Collectors.toList()));
                    } else {
                        as.setPackageName(Names.constructQualifiedName(astLanguageFamilyCompilationUnitA.get().getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit().getPackageList()));
                        as.addAllImports(astLanguageFamilyCompilationUnitA.get().getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit()
                                .getMCImportStatementList().stream()
                                .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                                .collect(Collectors.toList()));
                    }
                } else {
                    as.setPackageName(Names.constructQualifiedName(astLanguageFamilyCompilationUnitA.get().getLanguageFamilyCompilationUnit().getPackage().getPartsList()));

                    // add imports
//                    as.addAllImports(astLanguageComponentCompilationUnitA.get().getLanguageComponentCompilationUnit()
//                            .getMCImportStatementList().stream()
//                            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
//                            .collect(Collectors.toList()));
                }
//
                as.setEnclosingScope(this);
                addSubScope(as);
            }
        }
    }

}
