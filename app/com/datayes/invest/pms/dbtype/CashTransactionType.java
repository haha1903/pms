package com.datayes.invest.pms.dbtype;

public enum CashTransactionType implements DbValue<String> {

    CREDIT("CREDIT"), DEBIT("DEBIT");
    
    private final String dbValue;
    
    private CashTransactionType(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static CashTransactionType fromDbValue(String v) {
        for (CashTransactionType t : values()) {
            if (t.dbValue.equals(v)) {
                return t;
            }
        }
        return null;
    }
}
