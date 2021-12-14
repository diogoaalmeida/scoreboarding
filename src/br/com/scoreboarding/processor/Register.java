package br.com.scoreboarding.processor;

public class Register {
    private final String name;
    private String value;

    public Register(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
