package br.com.scoreboarding.processor;

public class PhysicalFunctionUnity {
    private final String name;
    private String value1;
    private String value2;
    private String resultMemory;

    public PhysicalFunctionUnity(String name) {
        this.name = name;
    }

    public void doOperation() {
        // operação da unidade funcional com value1 e value2
    }

    public void reset() {
        this.value1 = null;
        this.value2 = null;
        this.resultMemory = null;
    }

    public String getName() {
        return name;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getResultMemory() {
        return resultMemory;
    }
}
