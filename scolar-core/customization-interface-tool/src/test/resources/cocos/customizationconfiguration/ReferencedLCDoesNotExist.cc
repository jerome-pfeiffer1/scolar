package cocos.customizationconfiguration;

customization ReferencedLCDoesNotExist for languagefamily.SomeNonExistingLC {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }

  bind production Inv.OCLInv -> Invariante;
  assign max = 1;

}