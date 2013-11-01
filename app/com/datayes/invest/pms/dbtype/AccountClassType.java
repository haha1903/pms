package com.datayes.invest.pms.dbtype;

public enum AccountClassType implements DbValue<String> {
    CASH("CASH"),
    CREDIT("CREDIT"),
    INVESTMENT("INVESTMENT");

    private final String dbValue;

    AccountClassType(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static AccountClassType fromDbValue(String v) {
        for(AccountClassType t: AccountClassType.values()) {
            if(t.getDbValue().equals(v))
                return t;
        }
        return null;
    }
}