package cocos.customizationconfiguration;

customization ReferencedPPIsOfWrongType
    for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Komponente2Java;
  }

  bind wfrs Inv.OCLInv -> Invariante;

}