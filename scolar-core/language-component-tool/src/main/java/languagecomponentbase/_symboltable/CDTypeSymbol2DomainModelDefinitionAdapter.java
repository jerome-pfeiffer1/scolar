package languagecomponentbase._symboltable;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;

public class CDTypeSymbol2DomainModelDefinitionAdapter extends DomainModelDefinitionSymbol {


    private CDTypeSymbol original;

    public CDTypeSymbol2DomainModelDefinitionAdapter(CDTypeSymbol symbol) {
        super(symbol.getName());
        this.original = symbol;
    }

    @Override
    public String getName() {
        return original.getName();
    }

    public CDTypeSymbol getAdaptee() {
        return this.original;
    }
}
