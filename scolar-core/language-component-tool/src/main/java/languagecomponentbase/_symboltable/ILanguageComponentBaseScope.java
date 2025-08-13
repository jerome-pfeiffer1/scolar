package languagecomponentbase._symboltable;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface ILanguageComponentBaseScope extends ILanguageComponentBaseScopeTOP {


    @Override
    default List<DomainModelDefinitionSymbol> resolveAdaptedDomainModelDefinitionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<DomainModelDefinitionSymbol> predicate) {
        List<DomainModelDefinitionSymbol> adapters =
                new ArrayList<>();

        List<CDTypeSymbol> arcFeatures =
                resolveCDTypeLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, x -> true);


        for (CDTypeSymbol feature : arcFeatures) {

            if (getLocalDomainModelDefinitionSymbols().stream().filter(v -> v instanceof CDTypeSymbol2DomainModelDefinitionAdapter)
                    .noneMatch(v -> ((CDTypeSymbol2DomainModelDefinitionAdapter) v).getAdaptee().equals(feature))) {
                // instantiate the adapter
                DomainModelDefinitionSymbol adapter = new CDTypeSymbol2DomainModelDefinitionAdapter(feature);

                // filter by modifier and predicate
                if (modifier.includes(adapter.getAccessModifier()) && predicate.test(adapter)) {

                    // add the adapter to the result
                    adapters.add(adapter);

                    // add the adapter to the scope
                    this.add(adapter);
                }
            }
        }
        return adapters;

    }
}
