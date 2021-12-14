package br.com.scoreboarding.enums;

public enum OperationName {
    ld("Load"),
    muld("Mult"),
    divd("Div"),
    subd("Sub"),
    addd("Add");

    private final String name;

    OperationName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}