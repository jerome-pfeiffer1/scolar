package mc.lang.montiarcwithstatechartswithvariablewithoclinvariant.cocos;

import mc.lang.montiarcwithstatechartswithvariablewithoclinvariant._cocos.MontiArcWithStateChartsWithVariableWithOCLInvariantCoCoChecker;

public class MontiArcWithStateChartsWithVariableWithOCLInvariantCoCos {
  public static MontiArcWithStateChartsWithVariableWithOCLInvariantCoCoChecker createDefaultChecker() {
    MontiArcWithStateChartsWithVariableWithOCLInvariantCoCoChecker checker = new MontiArcWithStateChartsWithVariableWithOCLInvariantCoCoChecker();

    checker.addCoCo((mc.lang.montiArc._cocos.MontiArcASTComponentCoCo) new mc.lang.montiarc.cocos.PreventInnerComponents());
    checker.addCoCo((mc.lang.montiArc._cocos.MontiArcASTConnectorCoCo) new mc.lang.montiarc.cocos.ConnectorSourceAndTargetExistAndFit());

  return checker;
  }
}
