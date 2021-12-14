package br.com.scoreboarding.memory;

import br.com.scoreboarding.scoreboarding.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Memory {
    private final List<String> instructions;
    private final List<BarrierSRIF> barrierSRIFList;
    private final List<BarrierIFID> barrierIFIDList;
    private final List<BarrierIDEX> barrierIDEXList;
    private final List<BarrierEXWB> barrierEXWBList;

    public Memory(List<String> instructions) {
        this.instructions = instructions;
        this.barrierSRIFList = new ArrayList<>();
        this.barrierIFIDList = new ArrayList<>();
        this.barrierIDEXList = new ArrayList<>();
        this.barrierEXWBList = new ArrayList<>();
    }

    public Boolean existsInstructionsToLoad() {
        return !this.instructions.isEmpty();
    }

    public Boolean barrierHasData() {
        return !(this.barrierSRIFList.isEmpty() && this.barrierIFIDList.isEmpty() && this.barrierIDEXList.isEmpty() && this.barrierEXWBList.isEmpty());
    }

    public void updateBarrierOnSearch(String instruction) {
        this.instructions.remove(0);
        this.barrierSRIFList.add(new BarrierSRIF(instruction)); // write on barrier
    }

    public void updateBarrierOnIssue(BarrierSRIF barrierSRIF, Instruction instruction) {
        this.barrierSRIFList.remove(barrierSRIF);
        this.barrierIFIDList.add(new BarrierIFID(instruction.getSource()));  // write on barrier
    }

    public void updateBarrierOnReadOperands(BarrierIFID barrierIFID, Instruction instruction, String value1, String value2) {
        this.barrierIFIDList.remove(barrierIFID);
        this.barrierIDEXList.add(new BarrierIDEX(instruction.getSource(), instruction.getD(), instruction.getOperation(), value1, value2));  // write on barrier
    }

    public void updateBarrierOnExecute(BarrierIDEX barrierIFID, String resultMemory) {
        this.barrierIDEXList.remove(barrierIFID);
        this.barrierEXWBList.add(new BarrierEXWB(barrierIFID.getInstruction(), barrierIFID.getDest(), resultMemory));  // write on barrier
    }

    public void updateBarrierOnWriteResults(BarrierEXWB barrierEXWB) {
        this.barrierEXWBList.remove(barrierEXWB);
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public List<BarrierSRIF> getBarrierSRIFList() {
        return barrierSRIFList;
    }

    public List<BarrierIFID> getBarrierIFIDList() {
        return barrierIFIDList;
    }

    public List<BarrierIDEX> getBarrierIDEXList() {
        return barrierIDEXList;
    }

    public List<BarrierEXWB> getBarrierEXWBList() {
        return barrierEXWBList;
    }
}
