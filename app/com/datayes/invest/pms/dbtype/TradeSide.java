package com.datayes.invest.pms.dbtype;

/*
 * 股票
 *   BUY - 买入
 *   SELL - 卖出
 *   (股票没有SHORT和COVER)
 *
 * 股指期货
 *   BUY - 多头开仓
 *   SELL - 多头平仓
 *   SHORT - 空头开仓
 *   COVER - 空头平仓
 */
public enum TradeSide implements DbValue<String> {

    BUY("BUY"),
    SELL("SELL"),
    SHORT("SHORT"),
    COVER("COVER");

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
