/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aut._generator._producer.IAutGenerator;
import aut._generator._product.IAutomaton;
import aut._generator.action.BaseActionGenerator;
import aut._generator.action._producer.IAction2JavaGenerator;
import aut._generator.guard._producer.IGuardExpr2JavaGenerator;
import aut._generator.state.BaseStateGenerator;
import aut.automatongrammar._ast.ASTAutMain;
import aut.automatongrammar._ast.ASTIAction;
import aut.automatongrammar._ast.ASTIGuardExpr;
import aut.automatongrammar._ast.ASTIState;
import aut.automatongrammar._ast.ASTStateBase;
import aut.automatongrammar._ast.ASTTransition;
import de.monticore.ast.ASTNode;
import de.monticore.io.FileReaderWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AutGenerator implements IAutGenerator {
  
  protected Map<Class<? extends ASTNode>, IGuardExpr2JavaGenerator> astIGuardExprGens = new HashMap<>();
  protected Map<Class<? extends ASTNode>, IAction2JavaGenerator> astIActionGens = new HashMap<>();
  
  /**
   * Constructor for aut.generator.AutGenerator
   */
  public AutGenerator() {
  }

  /**
   * @return astIGuardExprGens
   */
  public Map<Class<? extends ASTNode>, IGuardExpr2JavaGenerator> getASTIGuardExprGens() {
    return this.astIGuardExprGens;
  }

  public Map<Class<? extends ASTNode>, IAction2JavaGenerator> getASTIActionGens() {
    return this.astIActionGens;
  }

  public void register(Class<? extends ASTNode> ep,
      IGuardExpr2JavaGenerator gen) {
    this.getASTIGuardExprGens().put(ep, gen);
  }

  public void register(Class<? extends ASTNode> ep,
                       IAction2JavaGenerator gen) {
    this.getASTIActionGens().put(ep, gen);
  }

  @Override
  public void generate(ASTAutMain node, Path path) {
    List<ASTIState> stateList = node.getIStateList();
    Map<ASTIAction, String> action2className = new HashMap<>();
    Map<ASTIGuardExpr, String> guard2className = new HashMap<>();
    Map<String, String> stateName2className = new HashMap<>();
    
    String initialState = getInitialState(node);
    
    for (ASTTransition transition : node.getTransitionList()) {
      BaseActionGenerator actiongenerators = new BaseActionGenerator();
      String actionClassName = actiongenerators.getActionClassName(transition.getIAction());
      action2className.put(transition.getIAction(), actionClassName);

      for (ASTIState astiState : node.getIStateList()) {
        BaseStateGenerator stateGen = new BaseStateGenerator();
        String stateName = stateGen.getStateName(astiState);
        String stateClassName = stateGen.getStateClassName(astiState);
        stateName2className.put(stateName, stateClassName);
      }
      
      IGuardExpr2JavaGenerator guardGen = astIGuardExprGens
          .get(transition.getIGuardExpr().getClass());
      String guardClassName = guardGen.getGuardExprClassName(transition.getIGuardExpr());
      guard2className.put(transition.getIGuardExpr(), guardClassName);
      
      (new BaseActionGenerator()).generate(transition.getIAction(),
          path);
      astIGuardExprGens.get(transition.getIGuardExpr().getClass())
          .generate(transition.getIGuardExpr(), path);
    }

    List<String> transitionConstructorCalls = new ArrayList<>();

    for (ASTTransition t : node.getTransitionList()) {
      transitionConstructorCalls.add(getPrintedTransConstructorCall(t,
              guard2className, action2className, stateList));
    }

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");

    Map<String, Object> input = new HashMap<>();
    input.put("node", node);
    input.put("transitions", transitionConstructorCalls);
    input.put("stateName2className", stateName2className);
    input.put("initialState", initialState);

    try {
      Template template = cfg.getTemplate("Automaton2Java.ftl");
      Writer fileWriter = new FileWriter(path.resolve(node.getName() + ".java").toString());

      template.process(input, new OutputStreamWriter(System.out));
      template.process(input, fileWriter);

    } catch (IOException | TemplateException e) {
      e.printStackTrace();
    }
    
    for (ASTIState state : stateList) {
      (new BaseStateGenerator()).generate(state, path);
    }
  }
  
  /**
   * TODO: Write me!
   * 
   * @param node
   * @return
   */
  private String getInitialState(ASTAutMain node) {
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
    
    String sourceStateClass = "new " + (new BaseStateGenerator()).getTargetInterface().getName() + "("
        + transition.getSource() + ")";
    String targetStateClass = "new " + (new BaseStateGenerator()).getTargetInterface().getName() + "("
        + transition.getTarget() + ")";
    String actionClass = "new "
        + (new BaseActionGenerator()).getTargetInterface().getName()
        + "(new "
        + actions.get(transition.getIAction()) + "())";
    String guardClass = "new "
        + astIGuardExprGens.get(transition.getIGuardExpr().getClass()).getTargetInterface()
            .getName()
        + "(new " // Cast is missing here and currently needs to be added in generated file
        + guards.get(transition.getIGuardExpr()) + "())";
    
    return "new Transition(" + sourceStateClass + ", " + targetStateClass + "," + guardClass + ", "
        + actionClass + ")";
  }

  @Override
  public Class<?> getTargetInterface() {
    return IAutomaton.class;
  }
  
}
