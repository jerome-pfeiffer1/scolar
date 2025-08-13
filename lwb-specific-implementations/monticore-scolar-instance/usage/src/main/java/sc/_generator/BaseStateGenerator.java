/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sc._generator;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.monticore.io.FileReaderWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import sc._generator._producer.IState2JavaGen;
import sc.statechart._ast.ASTIState;
import sc.statechart._ast.ASTStateBase;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class BaseStateGenerator implements IState2JavaGen {

  /**
   * @see sc._generator._producer.IState2JavaGen#generate(sc.statechart._ast.ASTIState, java.nio.file.Path)
   */
  @Override
  public void generate(ASTIState node, Path path) throws IOException {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");


    Map<String, Object> input = new HashMap<>();
    input.put("node", node);

    try {
      Template template = cfg.getTemplate("BaseState2Java.ftl");
      Path targetPath = path.resolve(((ASTStateBase) node).getName() + "State" + ".java");
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
  }

  /**
   * @see sc._generator._producer.IState2JavaGen#getStateClassName(sc.statechart._ast.ASTIState)
   */
  @Override
  public String getStateClassName(ASTIState state) {
    if(state instanceof ASTStateBase) {
      return ((ASTStateBase) state).getName() + "State";
    }
    return null;
  }

  /**
   * @see sc._generator._producer.IState2JavaGen#getStateName(sc.statechart._ast.ASTIState)
   */
  @Override
  public String getStateName(ASTIState state) {
    if(state instanceof ASTStateBase) {
      return ((ASTStateBase) state).getName();
    }
    return null;
  }

  /**
   * @see sc._generator._producer.IState2JavaGen#getTargetInterface()
   */
  @Override
  public Class<?> getTargetInterface() {
    return BaseStateDelegator.class;
  }
  
}
