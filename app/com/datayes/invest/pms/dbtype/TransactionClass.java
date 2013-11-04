package com.datayes.invest.pms.dbtype;

public enum TransactionClass implements DbValue<String> {
    
    CASH("CASH"), CORPACT("CORPACT"), TRADE("TRADE");
    
    private String dbValue;
    
    private TransactionClass(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static TransactionClass fromDbValue(String v) {
        for (TransactionClass tc : values()) {
            if (tc.dbValue.equals(v)) {
                return tc;
            }
        }
        return null;
    }
}
