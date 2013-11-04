package com.datayes.invest.pms.util.progress;

public class ProgressEvent {

    private final int percent;

    private final Object source;

    private final ProgressStatus status;

    private long timeElapsed = -1L;

    public int getPercent() {
        return percent;
    }

    public Object getSource() {
        return source;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public ProgressEvent(Object source, int percent, ProgressStatus status) {
        this.source = source;
        this.percent = percent;
        this.status = status;
    }
}
