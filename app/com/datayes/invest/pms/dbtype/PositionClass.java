package com.datayes.invest.pms.dbtype;


public enum PositionClass implements DbValue<String> {

    CASH("CASH"),
    SECURITY("SECURITY");

    private final String dbValue;

    PositionClass(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static PositionClass fromDbValue(String v) {
        for(PositionClass t: values()) {
            if(t.getDbValue().equals(v))
                return t;
        }
        return null;
    }
}
