package com.datayes.invest.pms.dbtype;

public enum RateType implements DbValue<Long> {
    
    StockCommission(1L),
    Stamp(2L),
    FutureCommission(3L),
    FutureTransactionFee(4L),
    FutureDeliveryCharges(5L),
    FutureMinMarginRatio(6L),
    RepoCommission(7L);

    private final Long dbValue;

    RateType(Long dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public Long getDbValue() {
        return dbValue;
    }

    public static RateType fromDbValue(Long v) {
        for(RateType t: RateType.values()) {
            if(v == t.dbValue)
                return t;
        }
        return null;
    }
}
