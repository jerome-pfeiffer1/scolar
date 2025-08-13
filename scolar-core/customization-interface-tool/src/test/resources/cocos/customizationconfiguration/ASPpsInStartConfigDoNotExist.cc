package cocos.customizationconfiguration;

customization ASPpsInStartConfigDoNotExist
    for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production SomeOtherPP;
  }
  
  bind production Inv.OCLInv -> NonExisitingCP;
}