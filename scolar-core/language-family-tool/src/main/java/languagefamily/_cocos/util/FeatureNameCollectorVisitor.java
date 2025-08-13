/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily._cocos.util;

import java.util.HashSet;
import java.util.Set;

import de.monticore.featurediagram._ast.ASTFeature;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor2;

/**
 * TODO: Write me!
 *
 * @author Michael Mutert
 * @author Jerome Pfeiffer
 *
 */
public class FeatureNameCollectorVisitor implements FeatureDiagramTraverser {
  
  private Set<String> featureNames = new HashSet<>();
  

  @Override
  public void visit(ASTFeature node) {
    featureNames.add(node.getName());
  }
  
  /**
   * @return featureNames
   */
  public Set<String> getFeatureNames() {
    return this.featureNames;
  }

  @Override
  public Set<Object> getTraversedElements() {
    return null;
  }

  @Override
  public void setTraversedElements(Set<Object> traversedElements) {

  }
}
