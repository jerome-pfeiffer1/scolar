package languagecomponentbase;

import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnitA;
import languagecomponentbase._symboltable.ILanguageComponentBaseArtifactScope;
import languagecomponentbase._symboltable.ILanguageComponentBaseGlobalScope;
import languagecomponentbase._symboltable.LanguageComponentBaseScopesGenitor;
import languagecomponentbase._visitor.LanguageComponentBaseTraverser;

public class LanguageComponentBaseTool extends  LanguageComponentBaseToolTOP {

    @Override
    public ILanguageComponentBaseArtifactScope createSymbolTable(ASTLanguageComponentCompilationUnitA node) {
        ILanguageComponentBaseGlobalScope globalScope = LanguageComponentBaseMill.globalScope();
        globalScope.clear();
        LanguageComponentBaseScopesGenitor genitor = LanguageComponentBaseMill.scopesGenitor();
        LanguageComponentBaseTraverser traverser = LanguageComponentBaseMill.traverser();
        traverser.setLanguageComponentBaseHandler(genitor);
        traverser.add4LanguageComponentBase(genitor);
        genitor.putOnStack(globalScope);
        ILanguageComponentBaseArtifactScope artifactScope;
//        if(node.isPresentLanguageComponentCompilationUnit()) {
            artifactScope = genitor.createFromAST(node);
            globalScope.addSubScope(artifactScope);
//        }
//        else if(node.isPresentCDCompilationUnit()) {
//            genitor.
//        }

        return artifactScope;
    }

}
