/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator;

import java.io.*;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.monticore.ast.ASTNode;
import de.monticore.io.FileReaderWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import sc._generator._producer.IAction2JavaGenerator;
import sc._generator._producer.IGuardExpr2JavaGenerator;
import sc._generator._producer.ISCGenerator;
import sc._generator._producer.IState2JavaGen;
import sc._generator._product.IStatechart;
import sc.statechart._ast.ASTAction;
import sc.statechart._ast.ASTIAction;
import sc.statechart._ast.ASTIGuardExpr;
import sc.statechart._ast.ASTIState;
import sc.statechart._ast.ASTSCMain;
import sc.statechart._ast.ASTStateBase;
import sc.statechart._ast.ASTTransition;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SCGenerator implements ISCGenerator {
  
  protected Map<Class<? extends ASTNode>, IAction2JavaGenerator> astIActionGens = new HashMap<>();
  
  protected Map<Class<? extends ASTNode>, IGuardExpr2JavaGenerator> astIGuardExprGens = new HashMap<>();
  
  protected Map<Class<? extends ASTNode>, IState2JavaGen> astIStateGens = new HashMap<>();
  
  /**
   * Constructor for sc.generator.SCGenerator
   */
  public SCGenerator() {
    this.register(ASTStateBase.class, new BaseStateGenerator());
    this.register(ASTAction.class, new BaseActionGenerator());
  }
  
  /**
   * @param actionInterpreter the actionInterpreter to set
   */
  public void setActionInterpreter(IActionInterpreter actionInterpreter) {
    ((BaseActionGenerator) this.getASTIActionGens().get(ASTAction.class))
        .setActionInterpreter(actionInterpreter);
  }
  
  /**
   * @return astIActionGens
   */
  public Map<Class<? extends ASTNode>, IAction2JavaGenerator> getASTIActionGens() {
    return this.astIActionGens;
  }
  
  /**
   * @return astIGuardExprGens
   */
  public Map<Class<? extends ASTNode>, IGuardExpr2JavaGenerator> getASTIGuardExprGens() {
    return this.astIGuardExprGens;
  }
  
  /**
   * @return astIStateGens
   */
  public Map<Class<? extends ASTNode>, IState2JavaGen> getASTIStateGens() {
    return this.astIStateGens;
  }
  
  public void register(Class<? extends ASTNode> ep, IAction2JavaGenerator gen) {
    this.getASTIActionGens().put(ep, gen);
  }
  
  public void register(Class<? extends ASTNode> ep,
      IGuardExpr2JavaGenerator gen) {
    this.getASTIGuardExprGens().put(ep, gen);
  }
  
  public void register(Class<? extends ASTNode> ep, IState2JavaGen gen) {
    this.getASTIStateGens().put(ep, gen);
  }
  
  /**
   * @see sc._generator._producer.ISCGenerator#generate(sc.statechart._ast.ASTSCMain,
   * java.nio.file.Path)
   */
  @Override
  public void generate(ASTSCMain node, Path path) throws IOException {
    List<ASTIState> stateList = node.getIStateList();
    Map<ASTIAction, String> action2className = new HashMap<>();
    Map<ASTIGuardExpr, String> guard2className = new HashMap<>();
    Map<String, String> stateName2className = new HashMap<>();
    
    String initialState = getInitialState(node);
    
    for (ASTTransition transition : node.getTransitionList()) {
      IAction2JavaGenerator actiongenerators = astIActionGens
          .get(transition.getIAction().getClass());
      String actionClassName = actiongenerators.getActionClassName(transition.getIAction());
      action2className.put(transition.getIAction(), actionClassName);
      
      for (ASTIState astiState : node.getIStateList()) {
        IState2JavaGen stateGen = astIStateGens.get(astiState.getClass());
        String stateName = stateGen.getStateName(astiState);
        String stateClassName = stateGen.getStateClassName(astiState);
        stateName2className.put(stateName, stateClassName);
      }
      
      IGuardExpr2JavaGenerator guardGen = astIGuardExprGens
          .get(transition.getIGuardExpr().getClass());
      String guardClassName = guardGen.getGuardExprClassName(transition.getIGuardExpr());
      guard2className.put(transition.getIGuardExpr(), guardClassName);
      
      astIActionGens.get(transition.getIAction().getClass()).generate(transition.getIAction(),
          path);
      astIGuardExprGens.get(transition.getIGuardExpr().getClass())
          .generate(transition.getIGuardExpr(), path);
    }

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");
    
   List<String> transitionConstructorCalls = new ArrayList<>();
    
    for (ASTTransition t : node.getTransitionList()) {
      transitionConstructorCalls.add(getPrintedTransConstructorCall(t,
          guard2className, action2className, stateList));
    }

    String transitionString = String.join("\n", transitionConstructorCalls);
    String stateName2classNameString = String.valueOf(stateName2className);

    Map<String, Object> input = new HashMap<>();
    input.put("node", node);
    input.put("transitions", transitionString);
    input.put("stateName2className", stateName2classNameString);
    input.put("initialState", initialState);

    try {
      Template template = cfg.getTemplate("Statechart2Java.ftl");
      Path targetPath = path.resolve(node.getName() + ".java");
      Writer fileWriter = new FileWriter(targetPath.toString());
      try {
        template.process(input, new OutputStreamWriter(System.out));
        template.process(input, fileWriter);
      }
      finally {
        fileWriter.close();
      }
    } catch (IOException | TemplateException e) {
      e.printStackTrace();
    }
    
    for (ASTIState state : stateList) {
      astIStateGens.get(state.getClass()).generate(state, path);
    }
    
  }
  
  /**
   * TODO: Write me!
   * 
   * @param node
   * @return
   */
  private String getInitialState(ASTSCMain node) {
    ASTStateBase initialState = (ASTStateBase) node.getIStateList().stream()
        .filter(s -> s instanceof ASTStateBase)
        .filter(s -> ((ASTStateBase) s).isInitial()).findFirst().get();
    return initialState.getName();
  }
  
  private String getPrintedTransConstructorCall(
      ASTTransition transition,
      Map<ASTIGuardExpr, String> guards,
      Map<ASTIAction, String> actions,
      List<ASTIState> states) {
    
    String sourceStateClass = "new " + getTargetInterface(transition.getSource(), states) + "("
        + transition.getSource() + ")";
    String targetStateClass = "new " + getTargetInterface(transition.getTarget(), states) + "("
        + transition.getTarget() + ")";
    String actionClass = "new "
        + astIActionGens.get(transition.getIAction().getClass()).getTargetInterface().getName()
        + "(new "
        + actions.get(transition.getIAction()) + "())";
    String guardClass = "new "
        + astIGuardExprGens.get(transition.getIGuardExpr().getClass()).getTargetInterface()
            .getName()
        + "(new "
        + guards.get(transition.getIGuardExpr()) + "())";
    
    return "new Transition(" + sourceStateClass + ", " + targetStateClass + "," + guardClass + ", "
        + actionClass + ")";
  }
  
  /**
   * TODO: Write me!
   * 
   * @param source
   * @param states
   * @return
   */
  private String getTargetInterface(String source, List<ASTIState> states) {
    for (ASTIState s : states) {
      if (astIStateGens.get(s.getClass()).getStateName(s).equals(source)) {
        return astIStateGens.get(s.getClass()).getTargetInterface().getName();
      }
    }
    return null;
  }
  
  /**
   * @see sc._generator._producer.ISCGenerator#getTargetInterface()
   */
  @Override
  public Class<?> getTargetInterface() {
    return IStatechart.class;
  }
  
}
