package com.datayes.invest.pms.dbtype;

public enum CashTransactionReason implements DbValue<String> {

    ACCTFEE("ACCTFEE"),
    ACCTINTEREST("ACCTINTEREST"),
    CAPMOVE("CAPMOVE"),
    CORPACTION("CORPACTION"),
    SECTRADE("SECTRADE");
    
    private final String dbValue;
    
    private CashTransactionReason(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static CashTransactionReason fromDbValue(String v) {
        for (CashTransactionReason r : values()) {
            if (r.dbValue.equals(v)) {
                return r;
            }
        }
        return null;
    }
}
