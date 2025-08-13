package languagecomponentbase._ast;

public class ASTLanguageComponentCompilationUnit extends ASTLanguageComponentCompilationUnitTOP {

    @Override
    public ASTLanguageComponentCompilationUnit deepClone(ASTLanguageComponentCompilationUnit result) {
        result = super.deepClone(result);
        result.setEnclosingScope(this.getEnclosingScope());
        result.setLanguageComponent(this.getLanguageComponent().deepClone());
        return result;
    }
}
