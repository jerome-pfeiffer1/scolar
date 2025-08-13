/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator.state;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


import aut.automatongrammar._ast.ASTIState;
import aut.automatongrammar._ast.ASTStateBase;
import de.monticore.io.FileReaderWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class BaseStateGenerator {

  public void generate(ASTIState node, Path path) {
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

  public String getStateClassName(ASTIState state) {
    if(state instanceof ASTStateBase) {
      return ((ASTStateBase) state).getName() + "State";
    }
    return null;
  }

  public String getStateName(ASTIState state) {
    if(state instanceof ASTStateBase) {
      return ((ASTStateBase) state).getName();
    }
    return null;
  }

  public Class<?> getTargetInterface() {
    return BaseStateDelegator.class;
  }
  
}
