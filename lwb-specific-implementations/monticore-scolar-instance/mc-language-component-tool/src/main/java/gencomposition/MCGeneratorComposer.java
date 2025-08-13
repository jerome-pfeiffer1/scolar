package gencomposition;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.Names;
import languagecomponentbase._ast.ASTParameter;
import util.GeneratorRegistration;

import java.util.List;
import java.util.Map;

public class MCGeneratorComposer {


    public String getComposedASTProdName(String composedGrammarName, String composedPackage, String epRule, String ppRule) {
        String ruleName = "AST" + Names.getSimpleName(ppRule) + Names.getSimpleName(epRule);
        String _package = composedPackage + "." + Names.getSimpleName(composedGrammarName).toLowerCase();
        return _package + "." + "_ast" + "." + ruleName;
    }

    public String generateProductAdapter(CDTypeSymbol source, CDTypeSymbol target, String _package, String sourcePackage,
                                         String targetPackage) {
        GeneratorSetup gs = new GeneratorSetup();
        GeneratorEngine ge = new GeneratorEngine(gs);
        StringBuilder generate = ge.generate("templates.ProductAdapter", source.getAstNode(), source, target, _package, sourcePackage, targetPackage);
        return generate.toString();
    }

    public String generateProducerAdapter(CDTypeSymbol source, CDTypeSymbol target, String _package, String sourcePackage,
                                   String targetPackage, String productAdapterName) {
        return "";
    }

    public String generateRegisterClass(String _package, String composedGenName, String superGenerator,
                                 Map<String, List<GeneratorRegistration>> generators, String composedGrammarName, String composedPackageName,
                                 Map<ASTParameter, String> param2Host, Map<String, String> param2value) {
        return "";
    }

    public String generateDomainModel(String _package, String composedGenName, String compName) {
        return "";
    }

}
