package ${package};

import ${SourceKindSymbolClass};
import ${TargetKindSymbolClass};

import ${TargetKindResolverInterface};
import ${PathSourceMill};
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
* Automatically generated class that generates the symbol resolver for the language aggregation.
* The composition takes place from extension point ${Source} to provision point ${Target}.
* The ${Source}2${Target}Resolver implements the interface I${Target}SymbolResolver from the target.
*/

public class ${Source}2${Target}Resolver implements I${Target}SymbolResolver {

    @Override
    public List <${Target}Symbol> resolveAdapted${Target}Symbol(
    boolean foundSymbols, String name, AccessModifier modifier,
    Predicate<${Target}Symbol> predicate){

        List <${Target}Symbol> r = new ArrayList<>();
        Optional<${Source}Symbol> s = ${SourceMill}.globalScope().resolve${Source}(name, modifier);

        if(s.isPresent()){
            ${Source}2${Target}Adapter a = new ${Source}2${Target}Adapter(s.get());
            if(predicate.test(a)){
                r.add(a);
            }
        }
        return r;
    }
}