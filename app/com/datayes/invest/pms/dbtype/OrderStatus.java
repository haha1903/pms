package com.datayes.invest.pms.dbtype;


public enum OrderStatus implements DbValue<String> {

    CREATED("CREATED"),
    PLACED("CREATED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED");

    private final String dbValue;

    private OrderStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return this.dbValue;
    }

    public static OrderStatus fromDbValue(String v) {
        for (OrderStatus s : values()) {
            if (s.dbValue.equals(v)) {
                return s;
            }
        }
        return null;
    }
}
