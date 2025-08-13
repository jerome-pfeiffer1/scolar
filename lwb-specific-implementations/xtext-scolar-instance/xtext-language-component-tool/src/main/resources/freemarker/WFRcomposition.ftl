${tc.signature("packageName", "composedValidatorName", "wfrMethods")}

package ${packageName};

public class ${composedValidatorName} extends Abstract${composedValidatorName} {

  <#list wfrMethods as m>
    <#assign extendedfqnValidator = m.getValidatorFqnClassName()>
    <#assign extendedSimpleValidatorName = m.getSimpleValidatorClassName()>
    <#assign extendedValidatorVariableName = extendedSimpleValidatorName?substring(0, 1)?lower_case + extendedSimpleValidatorName?substring(1)>
    <#assign ruleVariableName = m.getRuleName()?substring(0, 1)?lower_case + m.getRuleName()?substring(1)>
    private ${extendedfqnValidator} ${extendedValidatorVariableName} = new ${extendedfqnValidator}();

    @Check
    public void check${m.getMethodName()}(${m.getRuleName()} ${ruleVariableName}){
      this.${extendedValidatorVariableName}.check${m.getMethodName()}(${ruleVariableName});
    }
  </#list>

}