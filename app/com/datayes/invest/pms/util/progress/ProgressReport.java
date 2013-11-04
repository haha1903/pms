package com.datayes.invest.pms.util.progress;

public class ProgressReport {

    private ProgressStatus status;

    private long startTime;

    private int percent;

    private long timeElapsed;

    public ProgressReport(ProgressStatus status, long startTime, int percent, long timeElapsed) {
        this.status = status;
        this.startTime = startTime;
        this.percent = percent;
        this.timeElapsed = timeElapsed;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getPercent() {
        return percent;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }
}
