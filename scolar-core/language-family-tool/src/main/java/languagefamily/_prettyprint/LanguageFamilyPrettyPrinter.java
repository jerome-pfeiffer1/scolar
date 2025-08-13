package languagefamily._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import languagefamily._ast.ASTFeatureDeclaration;

public class LanguageFamilyPrettyPrinter extends LanguageFamilyPrettyPrinterTOP {
    public LanguageFamilyPrettyPrinter(IndentPrinter printer, boolean printComments) {
        super(printer, printComments);
    }

    @Override
    public void handle(languagefamily._ast.ASTLanguageFamily node) {
        getPrinter().print("family " + node.getName());
        getPrinter().print("{");
        getPrinter().println();
        getTraverser().handle(node.getFeatureDiagram());
        getPrinter().println();
        for (ASTFeatureDeclaration astFeatureDeclaration : node.getFeaturesList()) {
            getTraverser().handle(astFeatureDeclaration);
        }
        getPrinter().println();
        getPrinter().print("}");
    }

}


