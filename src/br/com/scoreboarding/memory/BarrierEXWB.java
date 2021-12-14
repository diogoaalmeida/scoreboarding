package br.com.scoreboarding.memory;

public class BarrierEXWB {
    private final String instruction;
    private final String register;
    private final String value;

    public BarrierEXWB(String instruction, String register, String value) {
        this.instruction = instruction;
        this.register = register;
        this.value = value;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getRegister() {
        return register;
    }

    public String getValue() {
        return value;
    }
}
