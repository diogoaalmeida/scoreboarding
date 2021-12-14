package br.com.scoreboarding.scoreboarding;

public class FunctionUnity {
    private final String name;
    private Boolean busy = Boolean.FALSE;
    private String operation;
    private String fi;
    private String fj;
    private String fk;
    private FunctionUnity qj;
    private FunctionUnity qk;
    private Boolean rj;
    private Boolean rk;
    private Integer time;

    public FunctionUnity(String name) {
        this.name = name;
    }

    public void reset() {
        this.busy = Boolean.FALSE;
        this.operation = null;
        this.fi = null;
        this.fj = null;
        this.fk = null;
        this.qj = null;
        this.qk = null;
        this.rj = null;
        this.rk = null;
        this.time = null;
    }

    public Boolean getBusy() {
        return busy;
    }

    public String getName() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getFi() {
        return fi;
    }

    public String getFj() {
        return fj;
    }

    public String getFk() {
        return fk;
    }

    public FunctionUnity getQj() {
        return qj;
    }

    public FunctionUnity getQk() {
        return qk;
    }

    public Boolean getRj() {
        return rj;
    }

    public Boolean getRk() {
        return rk;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setFi(String fi) {
        this.fi = fi;
    }

    public void setFj(String fj) {
        this.fj = fj;
    }

    public void setFk(String fk) {
        this.fk = fk;
    }

    public void setQj(FunctionUnity qj) {
        this.qj = qj;
    }

    public void setQk(FunctionUnity qk) {
        this.qk = qk;
    }

    public void setRj(Boolean rj) {
        this.rj = rj;
    }

    public void setRk(Boolean rk) {
        this.rk = rk;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
