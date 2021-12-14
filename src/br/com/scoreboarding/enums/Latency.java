package br.com.scoreboarding.enums;

public enum Latency {
    ld(1),
    muld(10),
    divd(40),
    subd(2),
    addd(2);

    private final int latency;

    Latency(int latency) {
        this.latency = latency;
    }

    public int getLatency() {
        return latency;
    }
}