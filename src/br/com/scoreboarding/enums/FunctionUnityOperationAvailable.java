package br.com.scoreboarding.enums;

public enum FunctionUnityOperationAvailable {
    ld("Integer"),
    muld("Mult1, Mult2"),
    divd("Divide"),
    subd("Add"),
    addd("Add");

    private final String fuName;

    FunctionUnityOperationAvailable(String fuName) {
        this.fuName = fuName;
    }

    public String getFuName() {
        return fuName;
    }
}