package com.datayes.invest.pms.dbtype;

public enum TradeSide implements DbValue<String> {
    
    BUY("BUY"),                // 多头(买)开仓
    SELL("SELL"),              // 多头(卖)平仓
    SHORT("SHORT"),            // 空头(买)开仓
    COVER("COVER");            // 空头(卖)平仓

    private final String dbValue;

    private TradeSide(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }

    public static TradeSide fromDbValue(String v) {
        TradeSide ret = null;
        for (TradeSide s : TradeSide.values()) {
            if (s.dbValue.equals(v)) {
                ret = s;
            }
        }
        return ret;
    }
}
