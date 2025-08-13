package customizationconfiguration._symboltable;

import customizationconfiguration.CustomizationConfigurationMill;
import customizationconfiguration._ast.ASTCCompilationUnitA;
import customizationconfiguration._parser.CustomizationConfigurationParser;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomizationConfigurationGlobalScope extends CustomizationConfigurationGlobalScopeTOP {

    private CustomizationConfigurationParser parser;
    private CustomizationConfigurationScopesGenitorDelegator customizationConfigurationScopesGenitor;


    public CustomizationConfigurationGlobalScope() {
        super();
    }

    public CustomizationConfigurationGlobalScope(MCPath modelPath, String fileExt) {
        super(modelPath, fileExt);
        this.parser = new CustomizationConfigurationParser();
        customizationConfigurationScopesGenitor = CustomizationConfigurationMill.scopesGenitorDelegator();

    }

    @Override
    public CustomizationConfigurationGlobalScope getRealThis() {
        return this;
    }

    @Override
    public void loadFileForModelName(String modelName) {
        String ext = getFileExt();
        java.util.Optional<java.net.URL> location = getSymbolPath().find(modelName, ext);

        if(location.isPresent() && !isFileLoaded(location.get().toString())){
            addLoadedFile(location.get().toString());
            Optional<ASTCCompilationUnitA> astCCCompilationUnitA = Optional.empty();
            Path p = null;
            try {
                p =  Paths.get(location.get().toURI());
                astCCCompilationUnitA = parser.parse(p.toString());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            if(astCCCompilationUnitA.isPresent()) {
                ICustomizationConfigurationArtifactScope as =
                        customizationConfigurationScopesGenitor.createFromAST(astCCCompilationUnitA.get());
                if(astCCCompilationUnitA.get().isPresentLanguageComponentCompilationUnitA()) {
                    if (astCCCompilationUnitA.get().getLanguageComponentCompilationUnitA().isPresentCDCompilationUnit()) {
                        as.setPackageName(Names.constructQualifiedName(astCCCompilationUnitA.get().getLanguageComponentCompilationUnitA().getCDCompilationUnit().getCDPackageList()));
                        // add imports
                        as.addAllImports(astCCCompilationUnitA.get().getLanguageComponentCompilationUnitA().getCDCompilationUnit()
                                .getMCImportStatementList().stream()
                                .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                                .collect(Collectors.toList()));
                    } else {
                        as.setPackageName(Names.constructQualifiedName(astCCCompilationUnitA.get().getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit().getPackageList()));
                        as.addAllImports(astCCCompilationUnitA.get().getLanguageComponentCompilationUnitA().getLanguageComponentCompilationUnit()
                                .getMCImportStatementList().stream()
                                .map(i -> new ImportStatement(i.getQName(), i.isStar()))
                                .collect(Collectors.toList()));
                    }
                } else {
                    as.setPackageName(Names.constructQualifiedName(astCCCompilationUnitA.get().getCCCompilationUnit().getPackage().getPartsList()));

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
