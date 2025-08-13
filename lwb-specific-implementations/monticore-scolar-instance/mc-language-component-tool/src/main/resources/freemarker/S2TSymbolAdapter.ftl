package ${package};

import ${SourceKindSymbolClass};
import ${TargetKindSymbolClass};

/**
* Automatically generated class that generates a symbol adapter for the language aggregation.
* The composition takes place from extension point ${Source} to provision point ${Target}.
* The ${Source}2${Target}Adapter extends the class ${Target}Symbol from the target and contains an attribute ${Source}Symbol from the source.
*/

public class ${Source}2${Target}Adapter extends ${Target}Symbol {

    private ${Source}Symbol original;

    public ${Source}2${Target}Adapter(${Source}Symbol symbol){
        super(symbol.getName());
        this.original = symbol;
    }

    @Override
    public String getName() {
        return original.getName();
    }

    public ${Source}Symbol getAdaptee() {
        return this.original;
    }
}