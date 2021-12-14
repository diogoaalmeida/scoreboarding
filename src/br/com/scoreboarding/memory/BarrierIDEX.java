package br.com.scoreboarding.memory;

public class BarrierIDEX {
    private final String instruction;
    private final String dest;
    private final String op;
    private final String dataRead1;
    private final String dataRead2;

    public BarrierIDEX(String instruction, String dest, String op, String dataRead1, String dataRead2) {
        this.instruction = instruction;
        this.dest = dest;
        this.op = op;
        this.dataRead1 = dataRead1;
        this.dataRead2 = dataRead2;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getDest() {
        return dest;
    }

    public String getDataRead1() {
        return dataRead1;
    }

    public String getDataRead2() {
        return dataRead2;
    }
}
