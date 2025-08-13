/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package timedexpr._generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.monticore.io.FileReaderWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import timedexpr.clockexpression._ast.ASTClockExpr;

public class TimedExpressionGenerator implements ITimedExprGenerator {

  /**
   * @see timedexpr._generator.ITimedExprGenerator#generate(timedexpr.clockexpression._ast.ASTClockExpr,
   * java.nio.file.Path)
   */
  @Override
  public void generate(ASTClockExpr c, Path path) {
    String name = getClassName(c);

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");


    Map<String, Object> input = new HashMap<>();
    input.put("node", c);
    input.put("name", name);

    try {
      Template template = cfg.getTemplate("TimedExpr2Java.ftl");
      Path targetPath = path.resolve((name + ".java"));
      Writer fileWriter = new FileWriter(targetPath.toString());

        template.process(input, new OutputStreamWriter(System.out));
        template.process(input, fileWriter);

        fileWriter.close();

    } catch (IOException | TemplateException e) {
      e.printStackTrace();
    }
  }

  /**
   * @see timedexpr._generator.ITimedExprGenerator#getTargetInterface()
   */
  @Override
  public Class<?> getTargetInterface() {
    return ITimedExpr.class;
  }
  
  public String getClassName(ASTClockExpr c) {
    String name = "";
    
    if (c.isEarlier()) {
      name += "Before";
    }
    else {
      name += "After";
    }
    name += c.getTime().getHours().getSource() + "" + c.getTime().getMinutes().getSource();
    return name;
  }
}
