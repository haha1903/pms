package com.datayes.invest.pms.dbtype;

public enum FutureType implements DbValue<Integer> {

    COMMODITY(1),
    STOCKINDEX(2);

    private final Integer dbValue;

    private FutureType(Integer v) {
        this.dbValue = v;
    }

    @Override
    public Integer getDbValue() {
        return dbValue;
    }

    public static FutureType fromDbValue(Integer v) {
        for (FutureType t : values()) {
            if (t.dbValue == v)
                return t;
        }
        return null;
    }
}
