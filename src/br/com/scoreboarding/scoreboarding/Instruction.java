package br.com.scoreboarding.scoreboarding;

import java.util.Objects;

public class Instruction {
    private String operation;
    private String d;
    private String offset;
    private String b;
    private String o1;
    private String o2;
    private String rb;
    private String source;

    public Instruction(String instruction) {
        if (Objects.nonNull(instruction)) {
            String[] split = instruction.replaceAll(",", "").split(" ");
            if (split.length > 0) {
                this.source = instruction;
                this.operation = split[0];
                this.d = split[1];
                if (split[0].equals("ld")) {
                    String[] offsetSplit = split[2].split("\\)");
                    this.offset = offsetSplit[0].replace("(", "");
                    this.rb = offsetSplit[1];
                } else {
                    this.o1 = split[2];
                    this.o2 = split[3];
                }
            }
        }
    }

    public String getOperation() {
        return operation;
    }

    public String getD() {
        return d;
    }

    public String getO1() {
        return o1;
    }

    public String getO2() {
        return o2;
    }

    public String getRb() {
        return rb;
    }

    public String getSource() {
        return source;
    }
}
