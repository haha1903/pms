package com.datayes.invest.pms.dbtype;

public enum IdName implements DbValue<String> {
    
    ACCOUNT_ID("ACCOUNT_ID"),
    BROKER_ID("BROKER_ID"),
    TRADER_ID("TRADER_ID");
    
    private final String dbValue;
    
    private IdName(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
    
    public static IdName fromDbValue(String v) {
        for (IdName i : values()) {
            if (i.dbValue.equals(v)) {
                return i;
            }
        }
        return null;
    }
}
