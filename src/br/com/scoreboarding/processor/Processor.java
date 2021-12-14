package br.com.scoreboarding.processor;

import br.com.scoreboarding.enums.FunctionUnityOperationAvailable;
import br.com.scoreboarding.enums.Latency;
import br.com.scoreboarding.memory.*;
import br.com.scoreboarding.scoreboarding.FunctionUnity;
import br.com.scoreboarding.scoreboarding.Instruction;
import br.com.scoreboarding.scoreboarding.RegisterResultStatus;
import br.com.scoreboarding.scoreboarding.Scoreboarding;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Processor {
    private final BufferedWriter writer;
    private final Boolean print = Boolean.FALSE;

    private int clock = 0;
    private final Scoreboarding scoreboarding;
    private final List<PhysicalFunctionUnity> physicalFunctionUnities;
    private final List<Register> registers;
    private final Memory memory;

    public Processor(Path path, Memory memory, List<String> instructionList) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(path.toString().replaceAll("asm", "out")));
        this.memory = memory;
        this.scoreboarding = new Scoreboarding(instructionList);
        this.physicalFunctionUnities = initializePhysicalFunctionUnities();
        this.registers = initializeRegisters();
        this.startPc();
    }

    private List<Register> initializeRegisters() {
        final List<Register> registers;
        registers = new ArrayList<>();
        registers.add(new Register("pc"));
        registers.add(new Register("rb"));
        for (int i = 1; i <= 12; i++) {
            registers.add(new Register("r" + i));
        }
        return registers;
    }

    private List<PhysicalFunctionUnity> initializePhysicalFunctionUnities() {
        final List<PhysicalFunctionUnity> physicalFunctionUnities;
        physicalFunctionUnities = new ArrayList<>();
        physicalFunctionUnities.add(new PhysicalFunctionUnity("Integer"));
        physicalFunctionUnities.add(new PhysicalFunctionUnity("Mult1"));
        physicalFunctionUnities.add(new PhysicalFunctionUnity("Mult2"));
        physicalFunctionUnities.add(new PhysicalFunctionUnity("Add"));
        physicalFunctionUnities.add(new PhysicalFunctionUnity("Divide"));
        return physicalFunctionUnities;
    }

    public void startPc() {
        this.registers
                .stream()
                .filter(register -> register.getName().equals("pc"))
                .findFirst()
                .ifPresent(pcRegister -> {
                    if (Objects.isNull(pcRegister.getValue())) {
                        pcRegister.setValue("1");
                    }
                });
    }

    public void clock() throws IOException {
        // continue enquanto conter instruções para ler ou enquanto tiver instruções nas barreiras
        while (this.memory.existsInstructionsToLoad() || this.memory.barrierHasData()) {
            pipeline();
        }

        this.writer.close();
    }

    public void pipeline() throws IOException {
        writeResults();
        execution();
        readOperands();
        issue();
        search();

        print();
        updateClock();
    }

    public void updateClock() {
        clock++;
    }

    public void search() {
        if (this.memory.existsInstructionsToLoad()) {
            String instruction = this.memory.getInstructions().get(0);
            this.memory.updateBarrierOnSearch(instruction);
            this.incrementPc();
        }

        // Solução encontrada para resolver problema de não paralelismo
        this.scoreboarding.setFuFinish(null);
        this.scoreboarding.setRegisterWrote(null);
    }

    public void incrementPc() {
        this.registers
                .stream()
                .filter(register -> register.getName().equals("pc"))
                .findFirst()
                .ifPresent(pcRegister -> {
                    if (Objects.isNull(pcRegister.getValue())) {
                        pcRegister.setValue("1");
                    } else {
                        pcRegister.setValue(String.valueOf(Integer.parseInt(pcRegister.getValue()) + 1));
                    }
                });
    }

    public void issue() {
        if (!this.memory.getBarrierSRIFList().isEmpty()) {
            BarrierSRIF barrierSRIF = this.memory.getBarrierSRIFList().get(0);
            Instruction instruction = new Instruction(barrierSRIF.getInstruction());

            // Solução encontrada para resolver problema de não paralelismo
            if (instruction.getD().equals(this.scoreboarding.getRegisterWrote()) ||
                    (Objects.nonNull(instruction.getO1()) && instruction.getO1().equals(this.scoreboarding.getRegisterWrote())) ||
                    (Objects.nonNull(instruction.getO2()) && instruction.getO2().equals(this.scoreboarding.getRegisterWrote()))) {
                return;
            }

            // Not Busy[FU]
            Optional<FunctionUnity> functionUnitNotBusyOptional = this.scoreboarding
                    .getFunctionUnityList()
                    .stream()
                    .filter(functionUnity -> !functionUnity.getBusy() &&
                            FunctionUnityOperationAvailable.valueOf(instruction.getOperation()).getFuName().contains(functionUnity.getName()))
                    .findFirst();

            // Not Result[D]
            Optional<RegisterResultStatus> registerResultStatusOptional = this.scoreboarding
                    .getRegisterList()
                    .stream()
                    .filter(rrs -> rrs.getName().equals(instruction.getD()) &&
                            Objects.isNull(rrs.getFunctionUnit()))
                    .findFirst();

            if (functionUnitNotBusyOptional.isPresent() && registerResultStatusOptional.isPresent()) {
                FunctionUnity functionUnity = functionUnitNotBusyOptional.get();

                // Solução encontrada para resolver problema de não paralelismo
                if (functionUnity.getName().equals(this.scoreboarding.getFuFinish())) {
                    return;
                }

                RegisterResultStatus registerResultStatus = registerResultStatusOptional.get();
                functionUnity.setBusy(Boolean.TRUE); // Busy[FU] <= yes
                functionUnity.setOperation(instruction.getOperation()); // Op[FU] <= op
                if (instruction.getOperation().equals("ld")) {
                    functionUnity.setFk(instruction.getRb()); // FK[FU] <= Rb
                } else {
                    functionUnity.setFj(instruction.getO1()); // Fj[FU] <= S1
                    functionUnity.setFk(instruction.getO2()); // Fk[FU] <= S2
                }
                functionUnity.setFi(instruction.getD()); // Fi[FU] <= d

                Optional<RegisterResultStatus> fuQjOptional = this.scoreboarding
                        .getRegisterList()
                        .stream()
                        .filter(rrs -> rrs.getName().equals(functionUnity.getFj()) &&
                                Objects.nonNull(rrs.getFunctionUnit()))
                        .findFirst();

                functionUnity.setQj(fuQjOptional.map(RegisterResultStatus::getFunctionUnit).orElse(null)); // Qj <= Result[S1]
                functionUnity.setRj(Objects.isNull(functionUnity.getQj())); // Rj <= not Qj

                Optional<RegisterResultStatus> fuQkOptional = this.scoreboarding
                        .getRegisterList()
                        .stream()
                        .filter(rrs -> rrs.getName().equals(functionUnity.getFk()) &&
                                Objects.nonNull(rrs.getFunctionUnit()))
                        .findFirst();

                functionUnity.setQk(fuQkOptional.map(RegisterResultStatus::getFunctionUnit).orElse(null)); // Qk <= Result[S2]
                functionUnity.setRk(Objects.isNull(functionUnity.getQk())); // Rk <= not Qk
                registerResultStatus.setFunctionUnit(functionUnity); // Result[D] <= FU

                this.memory.updateBarrierOnIssue(barrierSRIF, instruction);
                this.scoreboarding.updateInstructionsStatus(instruction.getSource(), String.valueOf(this.clock), "issue");
            }
        }
    }

    public void readOperands() {
        List<BarrierIFID> copy = new ArrayList<>(this.memory.getBarrierIFIDList());
        copy.forEach(barrierIFID -> {
            Instruction instruction = new Instruction(barrierIFID.getInstruction());
            FunctionUnity fu = this.scoreboarding.getFunctionUnityByInstruction(instruction.getD());
            if (Objects.nonNull(fu)) {
                // Solução encontrada para resolver problema de não paralelismo
                if ((Objects.nonNull(fu.getFj()) && fu.getFj().equals(this.scoreboarding.getRegisterWrote())) ||
                        (Objects.nonNull(fu.getFk()) && fu.getFk().equals(this.scoreboarding.getRegisterWrote()))) {
                    return;
                }

                // Rj and Rk
                if (fu.getRj() && fu.getRk()) {
                    fu.setRj(Boolean.FALSE);
                    fu.setRk(Boolean.FALSE);
                    fu.setQj(null);
                    fu.setQk(null);

                    // lendo registradores
                    String value1 = this.readRegister(instruction.getO1());
                    String value2 = this.readRegister(instruction.getO2());

                    this.memory.updateBarrierOnReadOperands(barrierIFID, instruction, value1, value2);
                    this.scoreboarding.updateInstructionsStatus(instruction.getSource(), String.valueOf(clock), "read");
                }
            }
        });
    }

    public void execution() {
        List<BarrierIDEX> copy = new ArrayList<>(this.memory.getBarrierIDEXList());
        copy.forEach(barrierIFID -> {
            FunctionUnity fu = this.scoreboarding.getFunctionUnityByInstruction(barrierIFID.getDest());

            if (Objects.nonNull(fu)) {
                PhysicalFunctionUnity physicalFunctionUnity = this.getPhysicalFunctionUnityByName(fu.getName());

                physicalFunctionUnity.setValue1(barrierIFID.getDataRead1());
                physicalFunctionUnity.setValue2(barrierIFID.getDataRead2());
                fu.setTime(Latency.valueOf(fu.getOperation()).getLatency());
                this.memory.updateBarrierOnExecute(barrierIFID, physicalFunctionUnity.getResultMemory());
            }
        });

        // atualizando variável que controla a parada da execução e executando unidade funcional física
        this.memory.getBarrierEXWBList().forEach(barrierEXWB -> {
            FunctionUnity fu = this.scoreboarding.getFunctionUnityByInstruction(barrierEXWB.getRegister());
            if (Objects.nonNull(fu.getTime())) {
                PhysicalFunctionUnity physicalFunctionUnity = this.getPhysicalFunctionUnityByName(fu.getName());
                physicalFunctionUnity.doOperation();

                if ((fu.getTime() - 1) >= 0) {
                    fu.setTime(fu.getTime() - 1);
                }
                if (fu.getTime() == 0) {
                    this.scoreboarding.updateInstructionsStatus(barrierEXWB.getInstruction(), String.valueOf(clock), "execution");
                }
            }
        });
    }

    public void writeResults() {
        List<BarrierEXWB> copy = new ArrayList<>(this.memory.getBarrierEXWBList());
        copy.forEach(barrierEXWB -> {
            FunctionUnity fu = this.scoreboarding.getFunctionUnityByInstruction(barrierEXWB.getRegister());

            if (Objects.nonNull(fu)) {
                // ∀f ((Fj[f] != Fi[FU] || Rj[f] = no) && (Fk[f] != Fi[FU] || Rk[f] = no))
                boolean dontWait = false;
                for (FunctionUnity f : this.scoreboarding.getFunctionUnityList()) {
                    if (Objects.nonNull(f.getRj()) && Objects.nonNull(f.getRk())) {
                        dontWait = (!Objects.equals(f.getFj(), fu.getFi()) || !f.getRj()) && (!Objects.equals(f.getFk(), fu.getFi()) || !f.getRk());
                    }
                }

                // controle de latência
                if (dontWait && fu.getTime() <= 0) {
                    PhysicalFunctionUnity physicalFunctionUnity = this.getPhysicalFunctionUnityByName(fu.getName());
                    physicalFunctionUnity.reset();

                    this.writeRegister(barrierEXWB.getRegister(), barrierEXWB.getValue());
                    this.scoreboarding.setRegisterWrote(fu.getFi());
                    this.scoreboarding.setFuFinish(fu.getName());
                    for (FunctionUnity f : this.scoreboarding.getFunctionUnityList()) {
                        if (Objects.nonNull(f.getQj()) && f.getQj().equals(fu)) {
                            f.setQj(null);
                            f.setRj(Boolean.TRUE);
                        }
                        if (Objects.nonNull(f.getQk()) && f.getQk().equals(fu)) {
                            f.setQk(null);
                            f.setRk(Boolean.TRUE);
                        }
                    }
                    this.scoreboarding
                            .getRegisterList()
                            .stream()
                            .filter(rrs ->
                                    Objects.nonNull(rrs.getFunctionUnit()) && rrs.getFunctionUnit().getName().equals(fu.getName()))
                            .findFirst()
                            .ifPresent(registerResultStatus -> registerResultStatus.setFunctionUnit(null));
                    fu.reset();
                    this.memory.updateBarrierOnWriteResults(barrierEXWB);
                    this.scoreboarding.updateInstructionsStatus(barrierEXWB.getInstruction(), String.valueOf(clock), "write");
                }
            }
        });
    }

    public PhysicalFunctionUnity getPhysicalFunctionUnityByName(String name) {
        return this.physicalFunctionUnities
                .stream()
                .filter(pfu -> pfu.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public String readRegister(String name) {
        return this.registers
                .stream()
                .filter(register -> register.getName().equals(name))
                .findFirst()
                .map(Register::getValue)
                .orElse(null);
    }

    public void writeRegister(String name, String value) {
        this.registers
                .stream()
                .filter(register -> register.getName().equals(name))
                .findFirst()
                .ifPresent(pcRegister -> pcRegister.setValue(value));
    }

    public void print() throws IOException {
        if (this.clock > 0) {
            String fmt = " ######################################################### ( Clock " + this.clock + " ) ######################################################### \n";
            if (this.print) {
                System.out.print(fmt);
            }
            this.writer.write(fmt);
            this.scoreboarding.printInstructionStatus(this.print, this.writer);
            this.scoreboarding.printFunctionUnitList(this.print, this.writer);
            this.scoreboarding.printRegisterResultStatus(this.print, this.writer);
        }
    }
}
