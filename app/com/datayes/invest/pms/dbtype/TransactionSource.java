package com.datayes.invest.pms.dbtype;

public enum TransactionSource implements DbValue<Integer> {
    
    OMS(1), PMS(2);
    
    private Integer dbValue;
    
    private TransactionSource(Integer dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public Integer getDbValue() {
        return dbValue;
    }

    public static TransactionSource fromDbValue(Integer v) {
        for (TransactionSource ts : values()) {
            if (ts.dbValue.equals(v)) {
                return ts;
            }
        }
        return null;
    }
}
