package cocos.customizationconfiguration;

customization BoundCPDoesNotExist for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }
  
  bind production Inv.OCLInv -> NonExisitingCP;
}