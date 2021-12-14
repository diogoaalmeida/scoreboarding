package br.com.scoreboarding.scoreboarding;

public class RegisterResultStatus {
    private final String name;
    private FunctionUnity functionUnity;

    public RegisterResultStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public FunctionUnity getFunctionUnit() {
        return functionUnity;
    }

    public void setFunctionUnit(FunctionUnity functionUnity) {
        this.functionUnity = functionUnity;
    }
}
