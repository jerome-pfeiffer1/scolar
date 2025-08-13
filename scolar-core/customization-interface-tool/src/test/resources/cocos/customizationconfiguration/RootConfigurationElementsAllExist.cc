package cocos.customizationconfiguration;

customization RootConfigurationElementsAllExist
    for general.montiarcexample.montiarc.MontiArc {

  language components {
    general.montiarcexample.invariant.OCLInvariant as Inv
  }

  root configuration {
    production Komponente;
    gen Verhalten2Java;
    wfrs BasicCoCos;
    wfrs NoInnerComponents;
  }

  bind production Inv.OCLInv -> NonExisitingCP;
}