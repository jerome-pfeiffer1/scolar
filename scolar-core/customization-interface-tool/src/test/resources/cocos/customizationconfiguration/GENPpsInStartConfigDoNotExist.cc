package cocos.customizationconfiguration;

customization GENPpsInStartConfigDoNotExist
    for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    gen SomeOtherPP;
  }
  
  bind production Inv.OCLInv -> NonExisitingCP;
}