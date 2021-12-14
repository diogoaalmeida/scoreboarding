package br.com.scoreboarding.scoreboarding;

import br.com.scoreboarding.enums.OperationName;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scoreboarding {
    private final String[][] instructionsStatus;
    private final List<FunctionUnity> functionUnityList;
    private final List<RegisterResultStatus> registerList;
    private String registerWrote; // identificação de qual registrador foi escrito no clock atual
    private String fuFinish; // identificação de qual unidade funcional foi escrita no clock atual

    public Scoreboarding(List<String> instructions) {
        this.functionUnityList = new ArrayList<>();
        this.registerList = new ArrayList<>();
        this.instructionsStatus = new String[instructions.size()][5];

        this.functionUnityList.add(new FunctionUnity("Integer"));
        this.functionUnityList.add(new FunctionUnity("Mult1"));
        this.functionUnityList.add(new FunctionUnity("Mult2"));
        this.functionUnityList.add(new FunctionUnity("Add"));
        this.functionUnityList.add(new FunctionUnity("Divide"));

        for (int i = 0; i < 12; i++) {
            this.registerList.add(new RegisterResultStatus("r" + (i + 1)));
        }

        for (int i = 0; i < instructions.size(); i++) {
            this.instructionsStatus[i][0] = instructions.get(i);
        }
        this.registerList.add(new RegisterResultStatus("rb"));
    }

    public void updateInstructionsStatus(String operation, String clock, String stage) {
        for (String[] row : this.instructionsStatus) {
            if (row[0].equals(operation)) {
                switch (stage) {
                    case "issue":
                        row[1] = clock;
                        break;
                    case "read":
                        row[2] = clock;
                        break;
                    case "execution":
                        row[3] = clock;
                        break;
                    case "write":
                        row[4] = clock;
                        break;
                }
            }
        }
    }

    public FunctionUnity getFunctionUnityByInstruction(String dest) {
        return this.registerList
                .stream()
                .filter(rrs -> rrs.getName().equals(dest) && Objects.nonNull(rrs.getFunctionUnit()))
                .findFirst()
                .map(RegisterResultStatus::getFunctionUnit)
                .orElse(null);
    }

    public void printInstructionStatus(Boolean print, BufferedWriter writer) throws IOException {
        int columns = instructionsStatus[0].length;
        int[] columnsWidths = new int[]{18, 4, 4, 4, 4};

        // Mapeando tamanhos de colunas
        for (String[] row : instructionsStatus) {
            for (int col = 0; col < columns; col++) {
                columnsWidths[col] = Math.max(columnsWidths[col], String.valueOf(row[col]).length());
            }
        }

        String fmt = String.format("|\t%18s %4s %4s %4s %4s\t|%n", "Instruction", "Issue", " Read", "Exec.", "Write");
        if (print) {
            System.out.print(" ------------- Instruction Status --------------\n");
            System.out.print(fmt);
        }
        writer.write(" ------------- Instruction Status --------------\n");
        writer.write(fmt);
        for (String[] row : instructionsStatus) {
            for (int i = 0; i < columns; i++) {
                fmt = String.format("%s%%%ss%s", i == 0 ? "|\t" : "  ", Math.max(columnsWidths[i], 4), i < columns - 1 ? "" : "\t|%n");
                writer.write(String.format(fmt, Objects.isNull(row[i]) ? "" : row[i] + " "));
                if (print) {
                    System.out.print(String.format(fmt, Objects.isNull(row[i]) ? "" : row[i] + " "));
                }
            }
        }

        fmt = " -----------------------------------------------\n";
        if (print) {
            System.out.print(fmt);
        }
        writer.write(fmt);
    }

    public void printFunctionUnitList(Boolean print, BufferedWriter writer) throws IOException {
        int[] columnsWidths = new int[11];

        // Mapeando tamanhos de colunas
        this.functionUnityList.forEach(functionUnity -> {
            columnsWidths[0] = Math.max(columnsWidths[0], String.valueOf(functionUnity.getTime()).length());
            columnsWidths[1] = Math.max(columnsWidths[1], String.valueOf(functionUnity.getName()).length());
            columnsWidths[2] = Math.max(columnsWidths[2], String.valueOf(functionUnity.getBusy()).length());
            columnsWidths[3] = Math.max(columnsWidths[3], String.valueOf(Objects.nonNull(functionUnity.getOperation()) ? OperationName.valueOf(functionUnity.getOperation()).getName() : null).length());
            columnsWidths[4] = Math.max(columnsWidths[4], String.valueOf(functionUnity.getFi()).length());
            columnsWidths[5] = Math.max(columnsWidths[5], String.valueOf(functionUnity.getFj()).length());
            columnsWidths[6] = Math.max(columnsWidths[6], String.valueOf(functionUnity.getFk()).length());
            columnsWidths[7] = Math.max(columnsWidths[7], String.valueOf(Objects.nonNull(functionUnity.getQj()) ? functionUnity.getQj().getName() : null).length());
            columnsWidths[8] = Math.max(columnsWidths[8], String.valueOf(Objects.nonNull(functionUnity.getQk()) ? functionUnity.getQk().getName() : null).length());
            columnsWidths[9] = Math.max(columnsWidths[9], String.valueOf(functionUnity.getRj()).length());
            columnsWidths[10] = Math.max(columnsWidths[10], String.valueOf(functionUnity.getRk()).length());
        });


        String fmt = " ------------------------- Functional Unit Status --------------------------\n";
        if (print) {
            System.out.print(fmt);
        }
        writer.write(fmt);
        fmt = String.format(
                String.format("|\t%%%ss %%%ss %%%ss %%%ss %%%ss %%%ss %%%ss %%%ss %%%ss %%%ss %%%ss\t|", columnsWidths[0], columnsWidths[1] + 1, columnsWidths[2] + 1, columnsWidths[3] + 1, columnsWidths[4] + 1, columnsWidths[5] + 1, columnsWidths[6] + 1, columnsWidths[7] + 1, columnsWidths[8] + 1, columnsWidths[9] + 1, columnsWidths[10] + 1) + "%n",
                "Time", "Name", "Busy", "Op", "Fi", "Fj", "Fk", "Qj", "Qk", "Rj", "Rk");
        if (print) {
            System.out.print(fmt);
        }
        writer.write(fmt);
        for (FunctionUnity functionUnity : this.functionUnityList) {
            if (print) {
                System.out.print(String.format(String.format("%s%%%ss%s", "|\t", columnsWidths[0], ""), Objects.isNull(functionUnity.getTime()) ? "" : functionUnity.getTime()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[1], ""), functionUnity.getName()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[2], ""), functionUnity.getBusy() ? "Yes" : " No"));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[3], ""), Objects.isNull(functionUnity.getOperation()) ? "" : OperationName.valueOf(functionUnity.getOperation()).getName()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[4], ""), Objects.isNull(functionUnity.getFi()) ? "" : functionUnity.getFi()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[5], ""), Objects.isNull(functionUnity.getFj()) ? "" : functionUnity.getFj()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[6], ""), Objects.isNull(functionUnity.getFk()) ? "" : functionUnity.getFk()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[7], ""), Objects.isNull(functionUnity.getQj()) ? "" : functionUnity.getQj().getName()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[8], ""), Objects.isNull(functionUnity.getQk()) ? "" : functionUnity.getQk().getName()));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[9], ""), Objects.isNull(functionUnity.getFj()) || Objects.isNull(functionUnity.getRj()) ? "" : (functionUnity.getRj() ? "Yes" : " No")));
                System.out.print(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[10], "\t|%n"), Objects.isNull(functionUnity.getFk()) || Objects.isNull(functionUnity.getRk()) ? "" : (functionUnity.getRk() ? "Yes" : " No")));
            }

            writer.write(String.format(String.format("%s%%%ss%s", "|\t", columnsWidths[0], ""), Objects.isNull(functionUnity.getTime()) ? "" : functionUnity.getTime()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[1], ""), functionUnity.getName()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[2], ""), functionUnity.getBusy() ? "Yes" : " No"));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[3], ""), Objects.isNull(functionUnity.getOperation()) ? "" : OperationName.valueOf(functionUnity.getOperation()).getName()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[4], ""), Objects.isNull(functionUnity.getFi()) ? "" : functionUnity.getFi()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[5], ""), Objects.isNull(functionUnity.getFj()) ? "" : functionUnity.getFj()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[6], ""), Objects.isNull(functionUnity.getFk()) ? "" : functionUnity.getFk()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[7], ""), Objects.isNull(functionUnity.getQj()) ? "" : functionUnity.getQj().getName()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[8], ""), Objects.isNull(functionUnity.getQk()) ? "" : functionUnity.getQk().getName()));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[9], ""), Objects.isNull(functionUnity.getFj()) || Objects.isNull(functionUnity.getRj()) ? "" : (functionUnity.getRj() ? "Yes" : " No")));
            writer.write(String.format(String.format("%s%%%ss%s", "  ", columnsWidths[10], "\t|%n"), Objects.isNull(functionUnity.getFk()) || Objects.isNull(functionUnity.getRk()) ? "" : (functionUnity.getRk() ? "Yes" : " No")));
        }

        fmt = " ---------------------------------------------------------------------------\n";
        if (print) {
            System.out.print(fmt);
        }
        writer.write(fmt);
    }

    public void printRegisterResultStatus(Boolean print, BufferedWriter writer) throws IOException {
        int[] columnsWidths = new int[14];

        // Mapeando tamanhos de colunas
        for (int i = 0; i < this.registerList.size(); i++) {
            columnsWidths[i] = Math.max(this.registerList.get(i).getName().length(), Objects.nonNull(this.registerList.get(i).getFunctionUnit()) ? this.registerList.get(i).getFunctionUnit().getName().length() : 0);
        }

        String fmt = " ---------------------------------------------------- Register Result Status ---------------------------------------------------- \n";
        if (print) {
            System.out.print(fmt);
        }
        writer.write(fmt);
        for (int line = 0; line < 2; line++) {
            for (int col = 0; col < this.registerList.size(); col++) {
                fmt = String.format("%s%%%ss%s", "| ", Math.max(columnsWidths[col], 7), col < this.registerList.size() - 1 ? " " : "|%n");
                if (line == 0) {
                    if (print) {
                        System.out.print(String.format(fmt, this.registerList.get(col).getName()));
                    }
                    writer.write(String.format(fmt, this.registerList.get(col).getName()));
                } else {
                    if (print) {
                        System.out.print(String.format(fmt, Objects.nonNull(this.registerList.get(col).getFunctionUnit()) ? this.registerList.get(col).getFunctionUnit().getName() : ""));
                    }
                    writer.write(String.format(fmt, Objects.nonNull(this.registerList.get(col).getFunctionUnit()) ? this.registerList.get(col).getFunctionUnit().getName() : ""));
                }
            }
        }
        fmt = " -------------------------------------------------------------------------------------------------------------------------------- \n\n\n";
        if (print) {
            System.out.print(fmt);
        }
        writer.write(fmt);
    }

    public List<FunctionUnity> getFunctionUnityList() {
        return functionUnityList;
    }

    public List<RegisterResultStatus> getRegisterList() {
        return registerList;
    }

    public String getRegisterWrote() {
        return registerWrote;
    }

    public void setRegisterWrote(String registerWrote) {
        this.registerWrote = registerWrote;
    }

    public String getFuFinish() {
        return fuFinish;
    }

    public void setFuFinish(String fuFinish) {
        this.fuFinish = fuFinish;
    }
}
