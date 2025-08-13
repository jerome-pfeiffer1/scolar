
package languagecomponentbase._prettyprint;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
//import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Names;
import languagecomponentbase._ast.*;
import java.util.List;


public  class LanguageComponentBasePrettyPrinter extends LanguageComponentBasePrettyPrinterTOP {

    public LanguageComponentBasePrettyPrinter(IndentPrinter printer, boolean printComments) {
        super(printer, printComments);
        MCBasicTypesMill.reset();
        MCBasicTypesMill.init();
    }



    @Override
    public  void handle (languagecomponentbase._ast.ASTRequiredGrammarExtension node) {
        getPrinter().print("requires ");
        if (node.getOptionality().equals(ASTOptionality.OPTIONAL)) {
            getPrinter().print("optional ");
        } else {
            getPrinter().print("mandatory ");
        }
        if (node.isPresentComposition()) {
            if (node.getComposition().equals(ASTComposition.EMBEDDING)) {
                getPrinter().print("embedding ");
            } else if (node.getComposition().equals(ASTComposition.AGGREGATION)) {
                getPrinter().print("aggregation ");
            }
        }
        getPrinter().print("production ");
        getPrinter().print(node.getName()+ " ");
        if (node.isPresentReferencedRule()) {
            getPrinter().print("for " + node.getReferencedRule());
        }
        getPrinter().print(";");
        getPrinter().println();
    }


    @Override
    public  void handle (languagecomponentbase._ast.ASTRequiredGenExtension node) {
        getPrinter().print("requires ");
        if (node.getOptionality().equals(ASTOptionality.OPTIONAL)) {
            getPrinter().print("optional ");
        } else {
            getPrinter().print("mandatory ");
        }
        if (node.isPresentComposition()) {
            if (node.getComposition().equals(ASTComposition.EMBEDDING)) {
                getPrinter().print("embedding ");
            } else if (node.getComposition().equals(ASTComposition.AGGREGATION)) {
                getPrinter().print("aggregation ");
            }
        }
        getPrinter().print("gen ");
        getPrinter().print(node.getName()+ " ");
        if (node.isPresentReferencedRule()) {
            getPrinter().print("for " + node.getReferencedRule());
        }
        getPrinter().print("{\n");
        getPrinter().indent();
        for (ASTGeneratorRef generatorReference : node.getGeneratorRefList()) {
            generatorReference.accept(getTraverser());
        }

        for (ASTProducerInterfaceRef astProducerInterface : node.getProducerInterfaceRefList()) {
            astProducerInterface.accept(getTraverser());
        }
        for (ASTProductInterfaceRef astProductInterface : node.getProductInterfaceRefList()) {
            astProductInterface.accept(getTraverser());
        }
        getPrinter().unindent();
        getPrinter().println("}");
    }

    @Override
    public void handle(ASTParameter node) {
        getPrinter().print("parameter ");
        if (node.getOptionality().equals(ASTOptionality.OPTIONAL)) {
            getPrinter().print("optional ");
        } else {
            getPrinter().print("mandatory ");
        }
//        String typePrinted = new MCBasicTypesFullPrettyPrinter(new IndentPrinter()).prettyprint(node.getMCType());
        String typePrinted = MCBasicTypesMill.prettyPrint(node.getMCType(), false);
        getPrinter().print(typePrinted + " " + node.getName());
        final List<String> referencedRule = node.getReference().getPartsList();
        getPrinter().print(" for ");
        if (node.isWfr()) {
            getPrinter().print("wfr ");
        } else {
            getPrinter().print("transformation ");
        }

        getPrinter().print(Names.constructQualifiedName(referencedRule) + ";");
        getPrinter().println();
    }

    public String prettyPrint(ASTLanguageComponent languageComponent) {
        handle(languageComponent);
        return getPrinter().getContent();
    }
}
