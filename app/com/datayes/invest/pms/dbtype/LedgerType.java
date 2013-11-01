package com.datayes.invest.pms.dbtype;

public enum LedgerType implements DbValue<Long> {
    
    SECURITY(1L, PositionClass.SECURITY),
    CASH(2L, PositionClass.CASH),
    COMMISSION(3L, PositionClass.CASH),
    MARGIN(4L, PositionClass.CASH),
    FUTURE_LONG(5L, PositionClass.SECURITY),
    FUTURE_SHORT(6L, PositionClass.SECURITY),
    PAYABLE_SETT_ACCOUNTS(7L, PositionClass.CASH),
    RECEIVABLE_SETT_ACCOUNTS(8L, PositionClass.CASH),
    SHARE(9L, null),
    PAYABLE_REPO_PRINCIPAL(10L, PositionClass.CASH),
    RECEIVABLE_REPO_PRINCIPAL(11L, PositionClass.CASH),
    PAYABLE_REPO_INTEREST(12L, PositionClass.CASH),
    RECEIVABLE_REPO_INTEREST(13L, PositionClass.CASH),
    REPO_LONG(14L, PositionClass.SECURITY),
    REPO_SHORT(15L, PositionClass.SECURITY);

    private final Long dbValue;

    private final PositionClass positionClass;

    LedgerType(Long dbValue, PositionClass positionClass) {
        this.dbValue = dbValue;
        this.positionClass = positionClass;
    }

    @Override
    public Long getDbValue() {
        return dbValue;
    }

    public PositionClass getPositionClass() {
        return positionClass;
    }

    public static LedgerType fromDbValue(Long v) {
        for(LedgerType t: LedgerType.values()) {
            if(v == t.getDbValue())
                return t;
        }
        return null;
    }
}
