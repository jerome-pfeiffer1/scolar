package customizationconfiguration._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.*;

public class ASTCustomizationConfiguration extends ASTCustomizationConfigurationTOP {

  public ASTCustomizationConfiguration() {
  }

  public ASTCustomizationConfiguration(
      String name,
      ASTMCQualifiedName languageComponent,
      ASTRootConfiguration rootConfiguration,
      List<ASTComponentBinding> componentBindings,
      List<ASTParameterAssignment> parameterAssignments) {
    super();
    this.setName(name);
    this.setLanguageComponent(languageComponent);
    this.setRootConfiguration(rootConfiguration);
    this.setComponentBindingList(componentBindings);
    this.setParameterAssignmentList(parameterAssignments);
  }



  public List<String> getSelectedWfrSetNames() {
    return this.rootConfiguration.getWfrSetList();
  }

  public List<String> getSelectedAsPpNames() {
    return this.rootConfiguration.getAsPPList();
  }

  public List<String> getSelectedGenPpNames() {
    return this.rootConfiguration.getGenPPList();
  }

}
