/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package aut._generator.action;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


import aut.automatongrammar._ast.ASTAction;
import aut.automatongrammar._ast.ASTIAction;
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
public class BaseActionGenerator {

  private IActionInterpreter actionInterpreter;

  public void generate(ASTIAction expr, Path path) {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");

    Map<String, Object> input = new HashMap<>();
    input.put("expr", expr);

    try {
      Template template = cfg.getTemplate("Action2Java.ftl");
      String name = "";
      if(((ASTAction)expr).isPresentExec()) {
        name = "Exec";
      }
      if(((ASTAction)expr).isPresentPrint()) {
        name = "Print";
      }
      if(((ASTAction)expr).isPresentSend()) {
        name = "Send";
      }
      name += "Action";
      Writer fileWriter = new FileWriter(path.toString() +"/"+ name + ".java");
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
   * @param actionInterpreter the actionInterpreter to set
   */
  public void setActionInterpreter(IActionInterpreter actionInterpreter) {
    this.actionInterpreter = actionInterpreter;
  }  
  

  public String getActionClassName(ASTIAction node) {
    if(node instanceof ASTAction) {
      if(((ASTAction) node).isPresentExec()) {
        return "ExecAction";
      }
      else if(((ASTAction) node).isPresentPrint()) {
        return "PrintAction";
      }
      else if(((ASTAction) node).isPresentPrint()) {
        return "PrintAction";
      }
    }
    return null;
  }

  public Class<?> getTargetInterface() {
    return BaseActionDelegator.class;
  }
  
}
