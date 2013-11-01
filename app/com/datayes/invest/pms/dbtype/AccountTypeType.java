package com.datayes.invest.pms.dbtype;

public enum AccountTypeType implements DbValue<Long> {
    EQUITY(1L),
    FIXED_INCOME(2L),
    ABSOLUTE_INCOME(3L),
    CASH_INCOME(4L);

    private final Long dbValue;

    private AccountTypeType(Long dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public Long getDbValue() {
        return dbValue;
    }

    public static AccountTypeType fromDbValue(Long v) {
        for (AccountTypeType t : AccountTypeType.values()) {
            if(v == t.getDbValue()) {
                return t;
            }
        }
        return null;
    }
}

