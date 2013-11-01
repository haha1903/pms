package com.datayes.invest.pms.dbtype;

public enum AssetClass implements DbValue<Integer> {

    FUND(1),
    FUTURE(2),
    EQUITY(3),
    BOND(4),
    CASH(5),
    REPO(6);

    private final Integer dbValue;

    private AssetClass(Integer v) {
        this.dbValue = v;
    }

    @Override
    public Integer getDbValue() {
        return dbValue;
    }

    public static AssetClass fromDbValue(Integer v) {
        AssetClass ret = null;
        for (AssetClass ac : values()) {
            if (ac.getDbValue().equals(v)) {
                ret = ac;
            }
        }
        return ret;
    }
}
