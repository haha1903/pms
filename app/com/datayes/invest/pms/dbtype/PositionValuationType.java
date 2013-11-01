package com.datayes.invest.pms.dbtype;


public enum PositionValuationType implements DbValue<Long> {

    MARKET(1L);

    private final Long dbValue;

    PositionValuationType(Long dbValue) {
        this.dbValue = dbValue;
    }

    public Long getDbValue() {
        return dbValue;
    }

    public static PositionValuationType fromDbValue(Long v) {
        for(PositionValuationType t: values()) {
            if(t.getDbValue().equals(v))
                return t;
        }
        return null;
    }
}
