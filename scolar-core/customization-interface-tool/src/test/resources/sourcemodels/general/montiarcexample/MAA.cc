package general.montiarcexample;

customization MAA for general.montiarcexample.AutomatenArchitekturLanguageProduct {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }
  
  bind production Inv.OCLInv -> Invariante;
  bind gen Inv.Inv2Java -> Invariante2Java;
  
  assign max = 1;

}