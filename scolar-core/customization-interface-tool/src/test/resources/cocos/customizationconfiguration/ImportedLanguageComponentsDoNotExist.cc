package cocos.customizationconfiguration;

customization ImportedLanguageComponentsDoNotExist for languagefamily.AutomatenArchitektur {

  language components {
    some.nonexisting.Component as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }
  
  bind production Inv.OCLInv -> Invariante;
  assign max = 1;

}