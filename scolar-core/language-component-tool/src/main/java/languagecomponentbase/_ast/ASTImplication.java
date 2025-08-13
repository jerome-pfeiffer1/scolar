/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagecomponentbase._ast;

import java.util.List;

/**
 * Class for wrapping implication relations between one source (name of pp) and
 * multiple targets (names of pps).
 *
 * @author Mutert
 * @author Pfeiffer
 */
public class ASTImplication extends ASTImplicationTOP {
  
  public ASTImplication() {
  }
  
  public ASTImplication(String source, List<String> targets) {
    super();
    setSource(source);
    setTargetList(targets);
  }

}
