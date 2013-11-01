package com.datayes.invest.pms.dbtype;

public enum FutureDeliveryMethod implements DbValue<Integer> {

    CASH_SETTLEMENT(1),
    NON_DELIVERY(2),
    PHYSICAL_DELIVERY(3);

    private final Integer dbValue;

    private FutureDeliveryMethod(Integer v) {
        this.dbValue = v;
    }

    @Override
    public Integer getDbValue() {
        return dbValue;
    }

    public static FutureDeliveryMethod fromDbValue(Integer v) {
        for (FutureDeliveryMethod m : values()) {
            if (m.dbValue.equals(v))
                return m;
        }
        return null;
    }
}
