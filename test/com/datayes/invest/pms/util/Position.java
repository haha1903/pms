package com.datayes.invest.pms.util;


abstract public class Position {

    private long positionId;

    private Long accountId;

    private int ledgerId;

    protected Position() {
    }

    public Position(long positionId, Long accountId, int ledgerId) {
        this.positionId = positionId;
        this.accountId = accountId;
        this.ledgerId = ledgerId;
    }

    public long getPositionId() {
        return positionId;
    }

    private void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public int getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(int ledgerId) {
        this.ledgerId = ledgerId;
    }
}
