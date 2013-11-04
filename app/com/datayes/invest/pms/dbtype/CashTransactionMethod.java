package com.datayes.invest.pms.dbtype;

public enum CashTransactionMethod implements DbValue<String> {
    
    TRANSFER("TRANSFER");
    
    private final String dbValue;
    
    private CashTransactionMethod(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static CashTransactionMethod fromDbValue(String v) {
        for (CashTransactionMethod m : values()) {
            if (m.dbValue.equals(v)) {
                return m;
            }
        }
        return null;
    }
}
