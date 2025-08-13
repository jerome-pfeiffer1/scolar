package cocos.customizationconfiguration;

customization BoundParameterDoesNotExist for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }
  
  bind production Inv.OCLInv -> Invariante;
  assign nonExistingName = 1;

}