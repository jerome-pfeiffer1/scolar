package mc.lang.montiarcwithstatechartswithvariable.cocos;

import mc.lang.montiarcwithstatechartswithvariable._cocos.MontiArcWithStateChartsWithVariableCoCoChecker;

public class MontiArcWithStateChartsWithVariableCoCos {
  public static MontiArcWithStateChartsWithVariableCoCoChecker createDefaultChecker() {
    MontiArcWithStateChartsWithVariableCoCoChecker checker = new MontiArcWithStateChartsWithVariableCoCoChecker();

    checker.addCoCo((mc.lang.stateCharts._cocos.StateChartsASTSCTransitionCoCo) new general.montiarcexample.statecharts.cocos.TransitionsCorrect());
    checker.addCoCo((mc.lang.montiArc._cocos.MontiArcASTConnectorCoCo) new mc.lang.montiarc.cocos.ConnectorSourceAndTargetExistAndFit());
    checker.addCoCo((mc.lang.montiArc._cocos.MontiArcASTComponentCoCo) new mc.lang.montiarc.cocos.PreventInnerComponents());

  return checker;
  }
}
