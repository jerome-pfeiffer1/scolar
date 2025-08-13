package general.montiarcexample.customizationconfiguration;

customization MAA for general.montiarcexample.customizationinterface.AutomatenArchitekturLanguageProduct {

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }

  bind production general.montiarcexample.invariant.OCLInvariant.OCLInv -> Invariante;
  bind gen general.montiarcexample.invariant.OCLInvariant.Inv2Java -> Invariante2Java;

  assign max = 1;

}