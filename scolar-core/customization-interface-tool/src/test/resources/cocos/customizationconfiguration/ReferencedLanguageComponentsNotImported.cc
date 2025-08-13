package cocos.customizationconfiguration;

customization ReferencedLanguageComponentsNotImported for languagefamily.AutomatenArchitektur {

  language components {
    invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }

  bind production OtherComponent.OCLInv -> Invariante;
  assign max = 1;

}