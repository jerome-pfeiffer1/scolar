package util;


public class ValidatorClassInfo {

    private String validatorFqnClassName;
    private String methodName;
    private String ruleName;

    public ValidatorClassInfo(String validatorFqnClassName, String methodName, String ruleName) {
        this.validatorFqnClassName = validatorFqnClassName;
        this.methodName = methodName;
        this.ruleName = ruleName;
    }


    public String getRuleName() {
        return ruleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getValidatorFqnClassName() {
        return validatorFqnClassName;
    }

    public String getSimpleValidatorClassName() {
        return validatorFqnClassName.substring(validatorFqnClassName.lastIndexOf(".") + 1);
    }


}
