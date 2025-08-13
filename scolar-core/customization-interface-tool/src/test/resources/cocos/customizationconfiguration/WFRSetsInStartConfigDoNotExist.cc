package cocos.customizationconfiguration;

customization WFRSetsInStartConfigDoNotExist
    for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    wfrs SomeOtherPP;
    wfrs SomePP, NoInnerComponents;
  }
  
  bind production Inv.OCLInv -> NonExisitingCP;
}